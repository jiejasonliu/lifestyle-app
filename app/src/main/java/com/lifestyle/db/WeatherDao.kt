package com.lifestyle.db

import androidx.room.*
import com.lifestyle.models.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weather: WeatherEntity)

    @Update
    fun update(weather: WeatherEntity)

    @Query("SELECT weather.weatherdata FROM weather WHERE weather.city = :city")
    fun queryByCity(city: String): String
}