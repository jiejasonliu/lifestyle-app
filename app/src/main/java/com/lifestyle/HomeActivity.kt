package com.lifestyle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var cardViewProfile : CardView
    private lateinit var cardViewBmi : CardView
    private lateinit var cardViewHiking : CardView
    private lateinit var cardViewWeather : CardView
    private lateinit var cardViewSettings : CardView
    private lateinit var cardViewLogout: CardView
    private lateinit var imageViewProfilePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        cardViewProfile = findViewById(R.id.cardViewProfile)
        cardViewBmi = findViewById(R.id.cardViewBmi)
        cardViewHiking = findViewById(R.id.cardViewHiking)
        cardViewWeather = findViewById(R.id.cardViewWeather)
        cardViewSettings = findViewById(R.id.cardViewSettings)
        cardViewLogout = findViewById(R.id.cardViewLogout)
        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture)

        // Set default profile picture
        imageViewProfilePicture.setImageResource(R.drawable.default_pp)

        cardViewProfile.setOnClickListener(this)
        cardViewBmi.setOnClickListener(this)
        cardViewHiking.setOnClickListener(this)
        cardViewWeather.setOnClickListener(this)
        cardViewSettings.setOnClickListener(this)
        cardViewLogout.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cardViewProfile -> {
                Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show()
            }

            R.id.cardViewBmi -> {
                Toast.makeText(this, "BMI Calculator Clicked", Toast.LENGTH_SHORT).show()
            }

            R.id.cardViewHiking -> {
                Toast.makeText(this, "Hiking Trails Clicked", Toast.LENGTH_SHORT).show()
            }

            R.id.cardViewWeather -> {
                Toast.makeText(this, "Weather Clicked", Toast.LENGTH_SHORT).show()
            }

            R.id.cardViewSettings -> {
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show()
            }

            R.id.cardViewLogout -> {
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}