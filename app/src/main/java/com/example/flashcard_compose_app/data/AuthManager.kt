package com.example.flashcard_compose_app.data

import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context? = null) {
    private val prefs: SharedPreferences? = context?.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_USER_ROLE = "user_role"
    }

    fun saveLoginState(userId: String, email: String, username: String, role: String) {
        prefs?.edit()
            ?.putBoolean(KEY_IS_LOGGED_IN, true)
            ?.putString(KEY_USER_ID, userId)
            ?.putString(KEY_USERNAME, username)
            ?.putString(KEY_EMAIL, email)
            ?.putString(KEY_USER_ROLE, role)
            ?.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs?.getBoolean(KEY_IS_LOGGED_IN, false) ?: false
    }

    fun getUserId(): String? {
        return prefs?.getString(KEY_USER_ID, null)
    }

    fun getUserName(): String? {
        return prefs?.getString(KEY_USERNAME, null)
    }

    fun getUserEmail(): String? {
        return prefs?.getString(KEY_EMAIL, null)
    }

    fun getUserRole(): String? {
        return prefs?.getString(KEY_USER_ROLE, null)
    }

    fun logout() {
        prefs?.edit()
            ?.clear()
            ?.apply()
    }
}
