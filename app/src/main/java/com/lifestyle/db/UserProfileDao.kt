package com.lifestyle.db

import androidx.room.*
import com.lifestyle.models.UserProfileEntity
import com.lifestyle.repositories.LoginRepository
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userProfile: UserProfileEntity)

    @Delete
    fun delete(userProfile: UserProfileEntity)

    @Update
    fun update(userProfile: UserProfileEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username LIMIT 1)")
    fun exists(username: String): Boolean   // blocking!

    // @param settingKey - the key in the `settings` table that contains the logged in username
    @Query("SELECT users.* FROM users join settings " +
                 "WHERE settings.`key` = :settingKey AND settings.value = users.username")
    fun queryLoggedInUser(settingKey: String): UserProfileEntity?
}