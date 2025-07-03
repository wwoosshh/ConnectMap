package com.example.restaurantapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.restaurantapp.models.User

class TokenManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "auth_token"
        private const val USER_ID_KEY = "user_id"
        private const val USERNAME_KEY = "username"
        private const val EMAIL_KEY = "email"
    }

    fun saveAuthData(token: String, user: User) {
        sharedPreferences.edit().apply {
            putString(TOKEN_KEY, token)
            putInt(USER_ID_KEY, user.id)
            putString(USERNAME_KEY, user.username)
            putString(EMAIL_KEY, user.email)
            apply()
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun getAuthHeader(): String? {
        val token = getToken()
        return if (token != null) "Bearer $token" else null
    }

    fun getCurrentUser(): User? {
        val userId = sharedPreferences.getInt(USER_ID_KEY, -1)
        if (userId == -1) return null

        val username = sharedPreferences.getString(USERNAME_KEY, "") ?: ""
        val email = sharedPreferences.getString(EMAIL_KEY, "") ?: ""

        return User(id = userId, username = username, email = email)
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun clearAuthData() {
        sharedPreferences.edit().apply {
            remove(TOKEN_KEY)
            remove(USER_ID_KEY)
            remove(USERNAME_KEY)
            remove(EMAIL_KEY)
            apply()
        }
    }
}