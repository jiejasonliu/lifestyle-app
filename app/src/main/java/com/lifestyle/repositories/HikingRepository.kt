package com.lifestyle.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lifestyle.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import android.location.Location
import com.lifestyle.extensions.forEach
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class HikingRepository private constructor(private val appContext: Context)  {

    var hikingTrails = MutableLiveData<ArrayList<MarkerOptions>?>()
    private lateinit var latlng: LatLng

    companion object Factory {
        private var instance: HikingRepository? = null

        fun getInstance(appContext: Context): HikingRepository {
            synchronized(this) {
                if (instance == null) {
                    instance = HikingRepository(appContext)
                }
            }
            return instance!!
        }
    }

    fun fetchHikingTrails(loc: Location) {
        // Create URL to find hiking trails near device location
        var stringUrl =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?keyword=hiking_trail&location=" +
                    loc.latitude + "," + loc.longitude +
                    "&radius=5000&types=tourist_attraction&sensor=true&key=" +
                    appContext.resources.getString(R.string.google_maps_key)

        // Download json data from URL
        try {
            // Get trail head locations on a new thread
            CoroutineScope(Dispatchers.IO).launch {
                // Call Google Places API
                val data = downloadUrl(stringUrl)

                // Parse the response
                val response = parseHikingResponse(JSONObject(data))

                // Go through each item from the response and create a list of map markers
                val optionsList = ArrayList<MarkerOptions>()
                response.forEach { trail ->
                    latlng = LatLng(trail["lat"]!!.toDouble(), trail["long"]!!.toDouble())
                    val options = MarkerOptions().position(latlng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)) as MarkerOptions
                    options.title(trail["name"])
                    optionsList.add(options)
                }

                // Go back to main thread and update mutable live data
                withContext(Dispatchers.Main) {
                    hikingTrails.postValue(optionsList)
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
    private fun parseHikingResponse(data: JSONObject): List<Map<String, String>> {
        val results = mutableListOf<Map<String, String>>()
        val trails = data.getJSONArray("results")

        trails.forEach { jsonObj ->
            var map = mutableMapOf<String, String>()

            map["name"] = jsonObj.getString("name")
            var loc = jsonObj.getJSONObject("geometry").getJSONObject("location")
            map["lat"] = loc.getString("lat")
            map["long"] = loc.getString("lng")

            results.add(map)
        }

        return results
    }
}