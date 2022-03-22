package com.lifestyle.repositories

import android.content.Context
import com.lifestyle.db.LifestyleDatabase
import com.lifestyle.models.UserProfileEntity

class UserProfileRepository private constructor (appContext: Context) {

    var userProfileDao = LifestyleDatabase.getInstance(appContext).userProfileDao()

    companion object Factory {
        private var instance: UserProfileRepository? = null

        fun getInstance(appContext: Context): UserProfileRepository {
            synchronized(this) {
                if (instance == null) {
                    instance = UserProfileRepository(appContext)
                }
            }
            return instance!! // Kotlin is unaware of JVM synchronization
        }
    }

    fun doesUserExistBlocking(username: String): Boolean = userProfileDao.exists(username)

    fun insertUser(userProfile: UserProfileEntity) = userProfileDao.insert(userProfile)

    fun updateUser(userProfile: UserProfileEntity) = userProfileDao.update(userProfile)
}