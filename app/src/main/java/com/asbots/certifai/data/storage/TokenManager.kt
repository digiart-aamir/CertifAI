package com.asbots.certifai.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {

    private val prefs: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "asbots_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        context.getSharedPreferences("asbots_secure_fallback_prefs", Context.MODE_PRIVATE)
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUserProfile(
        name: String,
        username: String?,
        email: String,
        avatar: String?,
        coins: Int = 0,
        completedNodes: Int = 0,
        totalNodes: Int = 36
    ) {
        prefs.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_USERNAME, username)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_AVATAR, avatar)
            .putInt(KEY_USER_COINS, coins)
            .putInt(KEY_USER_COMPLETED_NODES, completedNodes)
            .putInt(KEY_USER_TOTAL_NODES, totalNodes)
            .apply()
    }

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun getUserUsername(): String? = prefs.getString(KEY_USER_USERNAME, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserAvatar(): String? = prefs.getString(KEY_USER_AVATAR, null)
    fun getUserCoins(): Int = prefs.getInt(KEY_USER_COINS, 0)
    fun getUserCompletedNodes(): Int = prefs.getInt(KEY_USER_COMPLETED_NODES, 0)
    fun getUserTotalNodes(): Int = prefs.getInt(KEY_USER_TOTAL_NODES, 0)

    fun clearAuthToken() {
        prefs.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_USERNAME)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_AVATAR)
            .remove(KEY_USER_COINS)
            .remove(KEY_USER_COMPLETED_NODES)
            .remove(KEY_USER_TOTAL_NODES)
            .apply()
    }

    fun saveThemeMode(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_THEME_DARK, isDark).apply()
    }

    fun isThemeDark(): Boolean {
        return prefs.getBoolean(KEY_THEME_DARK, false)
    }

    fun hasToken(): Boolean = !getAuthToken().isNullOrBlank()

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_USERNAME = "user_username"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_AVATAR = "user_avatar"
        private const val KEY_USER_COINS = "user_coins"
        private const val KEY_USER_COMPLETED_NODES = "user_completed_nodes"
        private const val KEY_USER_TOTAL_NODES = "user_total_nodes"
        private const val KEY_THEME_DARK = "theme_dark_mode"
    }
}
