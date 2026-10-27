package com.asbots.certifai.data.network

import com.asbots.certifai.data.models.AuthResponse
import com.asbots.certifai.data.models.CertifaiRoadmapResponse
import com.asbots.certifai.data.models.GoogleSignInRequest
import com.asbots.certifai.data.models.ProfileResponse
import com.asbots.certifai.data.models.SignOutRequest
import com.asbots.certifai.data.models.UpdateProfileRequest
import com.asbots.certifai.data.models.UpdateSettingsRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AsbotsApiService {

    @POST("modules/users/controllers/AuthController?action=google_signin")
    suspend fun signInWithGoogle(
        @Body request: GoogleSignInRequest
    ): Response<AuthResponse>

    @GET("modules/users/controllers/AuthController?action=profile")
    suspend fun getProfile(
        @Header("Authorization") bearerToken: String
    ): Response<ProfileResponse>

    @POST("modules/users/controllers/AuthController?action=update_profile")
    suspend fun updateProfile(
        @Header("Authorization") bearerToken: String,
        @Body request: UpdateProfileRequest
    ): Response<ProfileResponse>

    @POST("modules/users/controllers/AuthController?action=update_settings")
    suspend fun updateSettings(
        @Header("Authorization") bearerToken: String,
        @Body request: UpdateSettingsRequest
    ): Response<ProfileResponse>

    @POST("modules/users/controllers/AuthController?action=logout")
    suspend fun signOut(
        @Header("Authorization") bearerToken: String,
        @Body request: SignOutRequest = SignOutRequest()
    ): Response<AuthResponse>

    @GET("modules/certifai/api/roadmap.php")
    suspend fun getRoadmap(
        @Header("Authorization") bearerToken: String? = null
    ): Response<CertifaiRoadmapResponse>
}
