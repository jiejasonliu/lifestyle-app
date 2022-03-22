package com.lifestyle.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.helpers.HourlyWeatherAdaptor
import com.lifestyle.R
import com.lifestyle.models.UserProfileEntity
import com.lifestyle.viewmodels.UserViewModel
import com.lifestyle.viewmodels.WeatherViewModel
import org.json.JSONArray
import org.json.JSONObject
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

    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    companion object {
        private var userCity: String? = null
        private var userCountry: String? = null
        private var userState: String? = null
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val user: UserProfileEntity? = userViewModel.loggedInUser.value
        if (user != null) {
            if (user.city.isNullOrEmpty()) {
                Toast.makeText(activity, "Please update location information", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
                return
            }
            else {
                userCity = user.city
                userCountry = user.country
                weatherViewModel.setCity(user.city!!)
            }
        }
        weatherViewModel.fetchData(requireContext())

        bindObservers()
    }

    private fun bindObservers() {
        // API response received
        weatherViewModel.weatherLiveData.observe(requireActivity()) {
            println("(WeatherFragment) Observer callback for: weatherLiveData")
            val weatherData = weatherViewModel.weatherLiveData.value
            if (weatherData != null) {
                updateWeather(weatherData)
            }
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