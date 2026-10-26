package com.asbots.certifai.data.models

import com.google.gson.annotations.SerializedName

data class GoogleSignInRequest(
    @SerializedName("action") val action: String = "google_signin",
    @SerializedName("id_token") val idToken: String
)

data class SignOutRequest(
    @SerializedName("action") val action: String = "logout"
)

data class AuthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String?,
    @SerializedName("auth_token") val authToken: String?,
    @SerializedName("user") val user: UserProfile?
)

data class ProfileResponse(
    @SerializedName("status") val status: String,
    @SerializedName("user") val user: UserProfile?
)

data class UserProfile(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("coins_balance") val coinsBalance: Int = 0,
    @SerializedName("completed_nodes") val completedNodes: Int = 0,
    @SerializedName("total_nodes") val totalNodes: Int = 0
)
