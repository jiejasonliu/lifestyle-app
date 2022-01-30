package com.lifestyle.models

import android.content.Context
import android.content.SharedPreferences

class LoginSession private constructor(private val appContext: Context) {

    companion object Factory {
        const val SHARED_PREF_NAME = "lifestyle_login_session"
        const val SESSION_USERNAME = "session_state"
        const val LOGGED_OUT_VALUE = "#LOGGED-OUT#"

        private var instance: LoginSession? = null

        fun getInstance(appContext: Context): LoginSession {
            if (instance == null) {
                synchronized(this) {
                    instance = LoginSession(appContext)
                }
            }
            return instance!! // Kotlin is unaware of JVM synchronization
        }
    }

    fun getLoggedInUser(): StoredUser? {
        if (isLoggedIn()) {
            println(getSharedPreferences().all)
            val username = getSharedPreferences().getString(SESSION_USERNAME, null)
            if (username != null) return StoredUser(appContext, username)
        }
        return null
    }

    fun isLoggedIn(): Boolean {
        return getSharedPreferences().getString(SESSION_USERNAME, null) != LOGGED_OUT_VALUE &&
                !getSharedPreferences().getString(SESSION_USERNAME, null).isNullOrBlank()
    }

    fun login(username: String) {
        val editor = getSharedPreferences().edit()
        editor.putString(SESSION_USERNAME, username)
        editor.apply()
    }

    fun logout() {
        val editor = getSharedPreferences().edit()
        editor.putString(SESSION_USERNAME, LOGGED_OUT_VALUE)
        editor.apply()
    }

    private fun getSharedPreferences(): SharedPreferences {
        return appContext.getSharedPreferences("lifestyle_login_session", Context.MODE_PRIVATE)
    }
}