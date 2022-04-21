package com.lifestyle.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="weather")
data class WeatherEntity(
    @PrimaryKey
    @ColumnInfo(name="city") val city: String,

    @ColumnInfo(name="weatherdata")  val weatherdata: String,

)