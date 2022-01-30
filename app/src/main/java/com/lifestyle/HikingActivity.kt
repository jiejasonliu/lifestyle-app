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


class HikingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
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
        options.title("You are here")

        // Zoom into device location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10F))

        // Add markers to map
        mMap.addMarker(options)

        // TODO: Show hiking trails near device location
        var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                loc.latitude + "," + loc.longitude +
                "&radius=5000&types=hiking_trails&sensor=true&key=" +
                resources.getString(R.string.google_maps_key)

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
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        else {
            // If yes permission, wait for map to ready and goto onMapReady callback
            client.lastLocation.addOnSuccessListener { location -> if (location != null) {
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
}
