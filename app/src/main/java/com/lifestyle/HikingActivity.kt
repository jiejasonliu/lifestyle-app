package com.lifestyle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lifestyle.databinding.ActivityHikingBinding
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.lifestyle.extensions.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject

class HikingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityHikingBinding
    private lateinit var client: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var latlng: LatLng
    private lateinit var loc: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHikingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        client = LocationServices.getFusedLocationProviderClient(this)

        // Get user location
        getCurrentLocation()
    }

    override fun onMapReady(mMap: GoogleMap) {
        // Get device location
        latlng = LatLng(loc.latitude, loc.longitude)

        // Set markers
        var options = MarkerOptions().position(latlng) as MarkerOptions

        // Label for markers
        options.title("You Are Here")

        // Zoom into device location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10F))

        // Add markers to map
        mMap.addMarker(options)

        // Create URL to find hiking trails near device location
        var stringUrl =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?keyword=hiking_trail&location=" +
                    loc.latitude + "," + loc.longitude +
                    "&radius=5000&types=tourist_attraction&sensor=true&key=" +
                    resources.getString(R.string.google_maps_key)

        // Download json data from URL
        try {
            // Get trail head locations on a new thread
            CoroutineScope(Dispatchers.IO).launch {
                // Call Google Places API
                val data = downloadUrl(stringUrl)

                // Parse the response
                val response = parseHikingResponse(JSONObject(data))

                // Go through each item from the response and create a list of map markers
                val optionsList = arrayListOf<MarkerOptions>()
                response.forEach { trail ->
                    latlng = LatLng(trail["lat"]!!.toDouble(), trail["long"]!!.toDouble())
                    options = MarkerOptions().position(latlng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)) as MarkerOptions
                    options.title(trail["name"])
                    optionsList.add(options)
                }

                // Go back to main thread and add markers to the map
                withContext(Dispatchers.Main) {
                    optionsList.forEach { option ->
                        mMap.addMarker(option)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getCurrentLocation() {
        // Check if device has permission for device location
        var task = if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If no permission, ask permission and override onRequestPermissionResult callback
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        } else {
            // If yes permission, wait for map to ready and goto onMapReady callback
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    loc = location
                    mapFragment.getMapAsync(this)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Given that the request code matches to the code above, if user gives permission get device location
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
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
