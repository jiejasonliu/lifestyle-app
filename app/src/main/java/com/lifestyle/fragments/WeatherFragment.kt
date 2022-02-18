package com.lifestyle.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.lifestyle.R
import com.lifestyle.models.LoginSession
import com.lifestyle.models.StoredUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

class WeatherFragment: Fragment() {

    private lateinit var textViewCity: TextView
    private lateinit var textViewCountry: TextView
    private lateinit var textViewTemperature: TextView
    private lateinit var imageViewWeatherIcon: ImageView
    private lateinit var textViewWeatherDescription: TextView
    private var lat: Float? = null
    private var long: Float? = null

    companion object {
        private var userCity: String? = null
        private var userCountry: String? = null
        private var userState: String? = null

        fun newInstance(user: StoredUser): WeatherFragment {
            userCity = user.city
            userCountry = user.country
            userState = user.state
            return WeatherFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view:View =  inflater.inflate(R.layout.fragment_weather, container, false)
        textViewCity = view.findViewById(R.id.textViewCity)
        textViewCountry = view.findViewById(R.id.textViewCountry)
        textViewTemperature= view.findViewById(R.id.textViewTemperature)
        imageViewWeatherIcon = view.findViewById(R.id.imageViewWeatherIcon)
        textViewWeatherDescription = view.findViewById(R.id.textViewWeatherDescription)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showWeather()
    }

    private fun showWeather() {
        if (userCity == null || userCountry == null) {
            Toast.makeText(activity, "Please update location information", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            return
        }

        // geocoding: location -> (lat, long)
        getLatLong()
    }

    private fun getLatLong() {
        // Geocode the location provided by the user
        val geocodeURL = "https://api.openweathermap.org/geo/1.0/direct" +
                "?q=${userCity}" +
                "&limit=1" +
                "&appid=${resources.getString(R.string.openweather_maps_api)}"
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
                        Toast.makeText(activity, "Could not find the location specified", Toast.LENGTH_SHORT).show()
                        requireActivity().finish()
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
                "&exclude=alerts,daily,hourly,minutely" +
                "&units=imperial" +
                "&appid=${resources.getString(R.string.openweather_maps_api)}"

        // Download json data from URL
        try {
            // Get weather data on IO thread
            withContext(Dispatchers.IO) {
                // Call Openweathermaps API
                val data = downloadUrl(stringUrl)

                // Parse the response
                val results = parseWeatherResponse(JSONObject(data))

                // Go back to main thread
                withContext(Dispatchers.Main) {
                    // Update UI from main thread
                    updateWeather(results)
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

    fun updateWeather(results: Map<String, String>) {
        textViewCity.setText(userCity)
        textViewCountry.setText(userCountry)
        textViewTemperature.setText(results["temp"] + "\u2109")
        textViewWeatherDescription.setText(results["desc"])
        imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_64)
    }
}