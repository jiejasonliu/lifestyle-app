package com.lifestyle.db

import androidx.room.*
import com.lifestyle.models.SettingsEntity

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(settings: SettingsEntity)

    @Update
    fun update(settings: SettingsEntity)

    @Query("SELECT settings.value FROM settings WHERE settings.`key` = :key")
    fun querySetting(key: String): String
}