package com.asbots.certifai.data.network

import com.asbots.certifai.data.models.AuthResponse
import com.asbots.certifai.data.models.GoogleSignInRequest
import com.asbots.certifai.data.models.ProfileResponse
import com.asbots.certifai.data.models.SignOutRequest
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

    @POST("modules/users/controllers/AuthController?action=logout")
    suspend fun signOut(
        @Header("Authorization") bearerToken: String,
        @Body request: SignOutRequest = SignOutRequest()
    ): Response<AuthResponse>
}
