package com.lifestyle.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lifestyle.R
import com.squareup.picasso.Picasso

class HourlyWeatherAdaptor(private var weatherItems: List<Triple<String, String, String>>): RecyclerView.Adapter<HourlyWeatherAdaptor.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewHourlyTime: TextView = view.findViewById(R.id.textViewHourlyTime)
        var textViewHourlyTemperature: TextView = view.findViewById(R.id.textViewHourlyTemperature)
        var imageViewHourlyWeatherIcon: ImageView = view.findViewById(R.id.imageViewHourlyWeatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly_weather, parent, false)
        return ViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val time = weatherItems[position].first
        val temp = weatherItems[position].second
        val iconURL = "https://openweathermap.org/img/w/" + weatherItems[position].third + ".png"

        holder.textViewHourlyTime.text = time
        holder.textViewHourlyTemperature.text = temp
        if (!weatherItems[position].third.isNullOrEmpty()) {
            Picasso.get().load(iconURL).fit().centerCrop().into(holder.imageViewHourlyWeatherIcon)
        }
    }
    override fun getItemCount(): Int {
        return weatherItems.size
    }
}