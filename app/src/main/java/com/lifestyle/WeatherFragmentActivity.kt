package com.lifestyle

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class WeatherFragmentActivity: Fragment() {

    private lateinit var textViewCity: TextView
    private lateinit var textViewCountry: TextView
    private lateinit var textViewTemperature: TextView
    private lateinit var imageViewWeatherIcon: ImageView
    private lateinit var textViewWeatherDescription: TextView

    companion object {
        fun newInstance(): WeatherFragmentActivity {
            return WeatherFragmentActivity()
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

        getWeatherData("Salt Lake City", "United States")
    }

    fun getWeatherData(userCity: String?, userCountry: String?) {
        // TODO: API call to get weather data
    }

    fun updateUI() {
        // TODO: From the response, update the UI
    }
}