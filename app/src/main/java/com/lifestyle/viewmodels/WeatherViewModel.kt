package com.lifestyle.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lifestyle.repositories.WeatherRepository

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    var weatherLiveData = MutableLiveData<Map<String, String>?>()
    private lateinit var userCity: String

    /**
     * Call this method to refresh/rebind live data
     */
    fun fetchData(appContext: Context) {
        fetchHikes(appContext)
    }

    fun setCity(userCity: String) {
        this.userCity = userCity
    }

    private fun fetchHikes(appContext: Context) {
        val weatherRepository = WeatherRepository.getInstance(appContext)
        weatherRepository.fetchWeatherData(userCity)
        weatherLiveData = weatherRepository.currentWeatherData
    }
}