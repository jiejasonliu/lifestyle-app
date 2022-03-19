package com.lifestyle.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lifestyle.models.LoginSession
import com.lifestyle.models.StoredUser

class LoginRepository private constructor(private val appContext: Context) {

    // not populated until LoginRepository::fetchLoggedInUser() is called
    var loggedInUser = MutableLiveData<StoredUser?>()

    companion object Factory {
        private var instance: LoginRepository? = null

        fun getInstance(appContext: Context): LoginRepository {
            synchronized(this) {
                if (instance == null) {
                    instance = LoginRepository(appContext)
                }
            }
            return instance!! // Kotlin is unaware of JVM synchronization
        }
    }

    fun doesUserExist(username: String): Boolean {
        return LoginSession.getInstance(appContext).doesUserExist(username)
    }

    fun fetchLoggedInUser() {
        val user = LoginSession.getInstance(appContext).getLoggedInUser()
        loggedInUser.value = user
    }

    fun isLoggedIn(): Boolean {
        return LoginSession.getInstance(appContext).isLoggedIn()
    }

    fun login(username: String) {
        LoginSession.getInstance(appContext).login(username)
        fetchLoggedInUser()
    }

    fun logout() {
        LoginSession.getInstance(appContext).logout()
        fetchLoggedInUser()
    }
}