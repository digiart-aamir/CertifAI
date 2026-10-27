package com.asbots.certifai.data.models

import com.google.gson.annotations.SerializedName

data class GoogleSignInRequest(
    @SerializedName("action") val action: String = "google_signin",
    @SerializedName("id_token") val idToken: String
)

data class SignOutRequest(
    @SerializedName("action") val action: String = "logout"
)

data class UpdateProfileRequest(
    @SerializedName("action") val action: String = "update_profile",
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("website") val website: String?
)

data class UpdateSettingsRequest(
    @SerializedName("action") val action: String = "update_settings",
    @SerializedName("theme") val theme: String,
    @SerializedName("receive_streak_alerts") val receiveStreakAlerts: Int,
    @SerializedName("receive_weekly_reminders") val receiveWeeklyReminders: Int,
    @SerializedName("receive_marketing") val receiveMarketing: Int
)

data class AuthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String?,
    @SerializedName("auth_token") val authToken: String?,
    @SerializedName("user") val user: UserProfile?
)

data class ProfileResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String?,
    @SerializedName("user") val user: UserProfile?
)

data class UserSettings(
    @SerializedName("theme") val theme: String = "system",
    @SerializedName("receive_streak_alerts") val receiveStreakAlerts: Int = 1,
    @SerializedName("receive_weekly_reminders") val receiveWeeklyReminders: Int = 1,
    @SerializedName("receive_marketing") val receiveMarketing: Int = 0
)

data class UserProfile(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("website") val website: String? = null,
    @SerializedName("role") val role: String? = "user",
    @SerializedName("status") val status: String? = "active",
    @SerializedName("is_verified") val isVerified: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("coins_balance") val coinsBalance: Int = 0,
    @SerializedName("completed_nodes") val completedNodes: Int = 0,
    @SerializedName("total_nodes") val totalNodes: Int = 36,
    @SerializedName("badges_count") val badgesCount: Int = 0,
    @SerializedName("certificates_count") val certificatesCount: Int = 0,
    @SerializedName("settings") val settings: UserSettings? = null
)
