package com.lifestyle.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lifestyle.db.LifestyleDatabase
import com.lifestyle.models.SettingsEntity
import com.lifestyle.models.UserProfileEntity

class LoginRepository private constructor(private val appContext: Context) {

    // not populated until LoginRepository::fetchLoggedInUser() is called
    var loggedInUser = MutableLiveData<UserProfileEntity?>()

    var userProfileDao = LifestyleDatabase.getInstance(appContext).userProfileDao()
    val settingsDao = LifestyleDatabase.getInstance(appContext).settingsDao()

    companion object Factory {
        const val SETTING_LOGGED_IN_KEY = "loggedInUser"

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

    fun fetchLoggedInUser() {
        val user = userProfileDao.queryLoggedInUser(SETTING_LOGGED_IN_KEY)
        loggedInUser.value = user
    }

    fun getLoggedInUsername() = settingsDao.querySetting(SETTING_LOGGED_IN_KEY)

    fun login(username: String) {
        settingsDao.update(SettingsEntity(SETTING_LOGGED_IN_KEY, username))
        fetchLoggedInUser()
    }

    fun logout() {
        settingsDao.update(SettingsEntity(SETTING_LOGGED_IN_KEY, "#LOGGED-OUT#"))
        fetchLoggedInUser()
    }
}