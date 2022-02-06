package com.lifestyle.models

import android.content.Context
import android.content.SharedPreferences

class LoginSession private constructor(private val appContext: Context) {

    companion object Factory {
        const val SHARED_PREF_NAME = "lifestyle_login_session"  // key => SharedPreference
        const val SESSION_USERNAME_KEY = "session_state"       // SP key => user who is logged in
        const val LOGGED_OUT_VALUE = "#LOGGED-OUT#"           // value for session_state if no login

        private var instance: LoginSession? = null

        fun getInstance(appContext: Context): LoginSession {
            synchronized(this) {
                if (instance == null) {
                    instance = LoginSession(appContext)
                }
            }
            return instance!! // Kotlin is unaware of JVM synchronization
        }
    }

    fun getLoggedInUser(): StoredUser? {
        if (isLoggedIn()) {
            val username = getSharedPreferences().getString(SESSION_USERNAME_KEY, null)
            if (username != null)
                return StoredUser(appContext, username)
        }
        return null
    }

    fun isLoggedIn(): Boolean {
        return getSharedPreferences().getString(SESSION_USERNAME_KEY, null) != LOGGED_OUT_VALUE &&
                !getSharedPreferences().getString(SESSION_USERNAME_KEY, null).isNullOrBlank()
    }

    fun login(username: String) {
        val editor = getSharedPreferences().edit()
        editor.putString(SESSION_USERNAME_KEY, username)
        editor.apply()
    }

    fun logout() {
        val editor = getSharedPreferences().edit()
        editor.putString(SESSION_USERNAME_KEY, LOGGED_OUT_VALUE)
        editor.apply()
    }

    private fun getSharedPreferences(): SharedPreferences {
        return appContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }
}