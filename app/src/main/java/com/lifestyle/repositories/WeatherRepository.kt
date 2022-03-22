package com.lifestyle.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lifestyle.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

class WeatherRepository private constructor(private val appContext: Context)  {

    var currentWeatherData = MutableLiveData<Map<String, String>?>()
    var hourlyWeatherData = MutableLiveData<JSONObject?>()
    private var lat: Float? = null
    private var long: Float? = null

    companion object Factory {
        private var instance: WeatherRepository? = null

        fun getInstance(appContext: Context): WeatherRepository {
            synchronized(this) {
                if (instance == null) {
                    instance = WeatherRepository(appContext)
                }
            }
            return instance!!
        }
    }

    fun fetchWeatherData(userCity: String) {
        getLatLong(userCity)
    }

    private fun getLatLong(userCity: String) {
        // Geocode the location provided by the user
        val geocodeURL = "https://api.openweathermap.org/geo/1.0/direct" +
                "?q=${userCity}" +
                "&limit=1" +
                "&appid=${appContext.resources.getString(R.string.openweather_maps_api)}"
        // Download json data from URL
        try {
            // Get weather data on IO thread
            CoroutineScope(Dispatchers.IO).launch {
                // Call Openweathermaps API
                val data = downloadUrl(geocodeURL)

                // Parse the response
                parseGeocodeResponse(JSONArray(data))
                if (lat == null || long == null) {
                    withContext(Dispatchers.Main) {
                        // TODO: What to do if location does not exist
                    }
                    return@launch
                }

                // Get weather data after geocoding
                getWeatherData()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private suspend fun getWeatherData() {
        // Call openweathermap API
        val stringUrl = "https://api.openweathermap.org/data/2.5/onecall" +
                "?lat=${lat}&lon=${long}" +
                "&exclude=alerts,daily,minutely" +
                "&units=imperial" +
                "&appid=${appContext.resources.getString(R.string.openweather_maps_api)}"

        // Download json data from URL
        try {
            // Get weather data on IO thread
            withContext(Dispatchers.IO) {
                // Call Openweathermaps API
                val data = downloadUrl(stringUrl)
                hourlyWeatherData.postValue(JSONObject(data))

                // Parse the response
                val results = parseWeatherResponse(JSONObject(data))

                // Go back to main thread
                withContext(Dispatchers.Main) {
                    // Update UI from main thread
                    currentWeatherData.postValue(results)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun downloadUrl(string: String): String {
        // Create URL from string
        val url: URL = URL(string)
        val inputStream: InputStream
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()

        inputStream = connection.inputStream

        // Convert input stream to string
        return inputStream.bufferedReader().use(BufferedReader::readText)
    }

    @Throws(JSONException::class)
    private fun parseWeatherResponse(data: JSONObject): Map<String, String>{
        // Get current weather information
        var results = mutableMapOf<String, String>()
        var currentWeather: JSONObject = data.getJSONObject("current")
        var currentDetails: JSONObject = currentWeather.getJSONArray("weather").getJSONObject(0)
        var currentTempString = currentWeather.getString("temp")

        // Round the temperature
        val currentTempRounded = currentTempString.toFloat().roundToInt()
        currentTempString = currentTempRounded.toString()

        // Add to results
        results["temp"] = currentTempString
        results["desc"] = currentDetails.getString("description")
        results["icon"] = currentDetails.getString("icon")
        return results
    }

    @Throws(JSONException::class)
    private fun parseGeocodeResponse(data: JSONArray){
        // Get geocode information
        if (data.length() > 0) {
            lat = (data[0] as JSONObject).getString("lat").toFloat()
            long = (data[0] as JSONObject).getString("lon").toFloat()
        }
    }
}