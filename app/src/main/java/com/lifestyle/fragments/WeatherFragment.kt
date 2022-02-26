package com.lifestyle.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.helpers.HourlyWeatherAdaptor
import com.lifestyle.R
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
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherFragment: Fragment() {

    private lateinit var textViewCity: TextView
    private lateinit var textViewCountry: TextView
    private lateinit var textViewTemperature: TextView
    private lateinit var imageViewWeatherIcon: ImageView
    private lateinit var textViewWeatherDescription: TextView
    private lateinit var recyclerHourlyWeather: RecyclerView
    private lateinit var hourlyWeatherAdaptor: HourlyWeatherAdaptor
    private var hourlyWeatherItems = ArrayList<Triple<String, String, String>>()
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
        recyclerHourlyWeather = view.findViewById(R.id.recyclerViewHourlyWeather)

        // Setup the recycler view adaptor
        hourlyWeatherAdaptor = HourlyWeatherAdaptor(hourlyWeatherItems)
        recyclerHourlyWeather.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerHourlyWeather.adapter = hourlyWeatherAdaptor

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
                "&exclude=alerts,daily,minutely" +
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
                    setHourlyWeather(JSONObject(data))
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

    fun updateWeather(results: Map<String, String>) {
        textViewCity.setText(userCity)
        textViewCountry.setText(userCountry)
        textViewTemperature.setText(results["temp"] + "\u2109")
        textViewWeatherDescription.setText(results["desc"])

        // Fetch Icon for weather description and load it into imageview
        var iconUrl:String = "https://openweathermap.org/img/w/" + results["icon"] + ".png"
        Picasso.get().load(iconUrl).fit().centerCrop().into(imageViewWeatherIcon)
    }

    private fun setHourlyWeather(data: JSONObject) {
        var hourlyData: JSONArray = data.getJSONArray("hourly")

        // For the next 24 hours, add items to recycler view
        for (i in 0..23) {
            val date:Date = Date(hourlyData.getJSONObject(i).getString("dt").toLong()*1000)
            val sdf:SimpleDateFormat = SimpleDateFormat("EE h:mm a")
            val temp:String = hourlyData.getJSONObject(i).getString("temp")
            val iconURL:String = hourlyData.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon")

            hourlyWeatherItems.add(Triple(sdf.format(date),temp+"\u2109",iconURL))
        }

        hourlyWeatherAdaptor.notifyDataSetChanged()
    }
}