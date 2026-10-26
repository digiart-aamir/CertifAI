package com.asbots.certifai.data.repository

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.asbots.certifai.data.models.GoogleSignInRequest
import com.asbots.certifai.data.models.UserProfile
import com.asbots.certifai.data.network.ApiClient
import com.asbots.certifai.data.network.AsbotsApiService
import com.asbots.certifai.data.storage.TokenManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val context: Context,
    private val apiService: AsbotsApiService = ApiClient.apiService,
    private val tokenManager: TokenManager = TokenManager(context)
) {
    private val credentialManager = CredentialManager.create(context)

    // Server Web Client ID from Google Cloud Console Specification
    private val webClientId = "834293250211-qeo0r7e2hn4tvspavsd1nlp7ilik8msc.apps.googleusercontent.com"

    suspend fun performGoogleSignIn(): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val realName = googleIdTokenCredential.displayName ?: googleIdTokenCredential.givenName ?: "Learner"
                val realEmail = googleIdTokenCredential.id
                val realAvatar = googleIdTokenCredential.profilePictureUri?.toString()

                val apiResponse = try {
                    apiService.signInWithGoogle(GoogleSignInRequest(idToken = idToken))
                } catch (e: Exception) {
                    null
                }

                val token = apiResponse?.body()?.authToken ?: "session_active_${System.currentTimeMillis()}"
                val profile = apiResponse?.body()?.user ?: UserProfile(
                    id = 1,
                    name = realName,
                    username = realEmail.substringBefore("@"),
                    email = realEmail,
                    avatar = realAvatar,
                    role = "user",
                    status = "active",
                    isVerified = true,
                    createdAt = null,
                    coinsBalance = 150,
                    completedNodes = 8,
                    totalNodes = 36
                )

                tokenManager.saveAuthToken(token)
                tokenManager.saveUserProfile(profile.name, profile.email, profile.avatar)
                Result.success(profile)
            } else {
                Result.failure(Exception("Unexpected credential type received."))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Sign-in was cancelled."))
        } catch (e: NoCredentialException) {
            Result.failure(Exception("NO_CREDENTIALS_ON_DEVICE"))
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Google Sign-in failed: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun performDevSignIn(): UserProfile {
        val devProfile = UserProfile(
            id = 42,
            name = "Demo Learner",
            username = "demolearner",
            email = "demo.learner@asbots.com",
            avatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
            role = "user",
            status = "active",
            isVerified = true,
            createdAt = "2026-10-25 18:00:00",
            coinsBalance = 150,
            completedNodes = 8,
            totalNodes = 36
        )
        tokenManager.saveAuthToken(DEV_TOKEN)
        tokenManager.saveUserProfile(devProfile.name, devProfile.email, devProfile.avatar)
        return devProfile
    }

    suspend fun fetchCurrentProfile(): Result<UserProfile> = withContext(Dispatchers.IO) {
        val token = tokenManager.getAuthToken()
            ?: return@withContext Result.failure(Exception("Not logged in"))

        val savedName = tokenManager.getUserName() ?: "Learner"
        val savedEmail = tokenManager.getUserEmail() ?: "user@asbots.com"
        val savedAvatar = tokenManager.getUserAvatar()

        val localProfile = UserProfile(
            id = 1,
            name = savedName,
            username = savedEmail.substringBefore("@"),
            email = savedEmail,
            avatar = savedAvatar,
            role = "user",
            status = "active",
            isVerified = true,
            createdAt = null,
            coinsBalance = 150,
            completedNodes = 8,
            totalNodes = 36
        )

        try {
            val response = apiService.getProfile("Bearer $token")
            if (response.isSuccessful && response.body()?.user != null) {
                val serverUser = response.body()!!.user!!
                tokenManager.saveUserProfile(serverUser.name, serverUser.email, serverUser.avatar)
                Result.success(serverUser)
            } else {
                Result.success(localProfile)
            }
        } catch (e: Exception) {
            Result.success(localProfile)
        }
    }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = tokenManager.getAuthToken()
            if (token != null && token != DEV_TOKEN) {
                try {
                    apiService.signOut("Bearer $token")
                } catch (_: Exception) {
                }
            }
            tokenManager.clearAuthToken()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            Result.success(Unit)
        } catch (e: Exception) {
            tokenManager.clearAuthToken()
            Result.success(Unit)
        }
    }

    fun isUserLoggedIn(): Boolean = tokenManager.hasToken()

    companion object {
        private const val DEV_TOKEN = "demo_dev_token_asbots_2026_test_account"
    }
}
