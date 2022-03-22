package com.lifestyle.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="settings")
data class SettingsEntity (
    @PrimaryKey
    @ColumnInfo(name = "key") val key: String,

    @ColumnInfo(name = "value") val value: String,
)