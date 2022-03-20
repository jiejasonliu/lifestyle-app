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
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.lifestyle.viewmodels.HikingViewModel

class HikingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityHikingBinding
    private lateinit var client: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var latlng: LatLng
    private lateinit var loc: Location

    private val hikingViewModel: HikingViewModel by viewModels()

    companion object {
        const val ACCESS_FINE_LOC = Manifest.permission.ACCESS_FINE_LOCATION
        const val ACCESS_COARSE_LOC = Manifest.permission.ACCESS_COARSE_LOCATION
        const val PERM_GRANTED = PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHikingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        client = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()
    }

    private fun bindObservers(mMap: GoogleMap) {
        // user data changed
        hikingViewModel.hikingLiveData.observe(this) {
            println("(HikingActivity) Observer callback for: hikingLiveData")
            val markerList = hikingViewModel.hikingLiveData.value
            if (!markerList.isNullOrEmpty()) {
                markerList.forEach { option ->
                    mMap.addMarker(option)
                }
            }
        }
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

        // Set loc and fetch data
        hikingViewModel.setLocation(loc)
        hikingViewModel.fetchData(applicationContext)

        // bind observers from view models
        bindObservers(mMap)
    }

    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOC) == PERM_GRANTED ||
                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOC) == PERM_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        // check if device has location enabled; if not, show a toast
        val lm: LocationManager? =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (lm != null && !lm.isLocationEnabled) {
            Toast.makeText(
                applicationContext,
                "Error: Location Services must be enabled!",
                Toast.LENGTH_LONG
            ).show()
        }

        // Check if device has permission for device location
        var task =
            if (!hasLocationPermissions()) {
                // If no permission, ask permission and override onRequestPermissionResult callback
                ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOC), 1)
                return
            } else {
                Toast.makeText(
                    applicationContext,
                    "Searching For Nearby Hiking Trails",
                    Toast.LENGTH_LONG
                ).show()
                // If yes permission, wait for map to ready and goto onMapReady callback
                client.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        return CancellationTokenSource().token
                    }

                    override fun isCancellationRequested(): Boolean {
                        return false
                    }
                }).addOnSuccessListener { location ->
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
}
