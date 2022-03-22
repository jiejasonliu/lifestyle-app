package com.lifestyle.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lifestyle.repositories.WeatherRepository
import org.json.JSONObject

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    var weatherLiveData = MutableLiveData<Map<String, String>?>()
    var hourlyLiveData = MutableLiveData<JSONObject?>()
    private lateinit var userCity: String

    /**
     * Call this method to refresh/rebind live data
     */
    fun fetchData(appContext: Context) {
        fetchWeather(appContext)
    }

    fun setCity(userCity: String) {
        this.userCity = userCity
    }

    private fun fetchWeather(appContext: Context) {
        val weatherRepository = WeatherRepository.getInstance(appContext)
        weatherRepository.fetchWeatherData(userCity)
        weatherLiveData = weatherRepository.currentWeatherData
        hourlyLiveData = weatherRepository.hourlyWeatherData
    }
}