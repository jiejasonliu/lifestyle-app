package com.lifestyle.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.lifestyle.repositories.HikingRepository
import com.google.android.gms.maps.model.MarkerOptions
import android.location.Location

class HikingViewModel(application: Application) : AndroidViewModel(application) {
    var hikingLiveData = MutableLiveData<ArrayList<MarkerOptions>?>()
    private var loc = Location("dummy-provider")

    /**
     * Call this method to refresh/rebind live data
     */
    fun fetchData(appContext: Context) {
        fetchHikes(appContext)
    }

    fun setLocation(loc: Location){
        this.loc.latitude = loc.latitude
        this.loc.longitude = loc.longitude
    }

    private fun fetchHikes(appContext: Context) {
        val hikingRepository = HikingRepository.getInstance(appContext)
        hikingRepository.fetchHikingTrails(this.loc)
        hikingLiveData = hikingRepository.hikingTrails
    }
}