package com.lifestyle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import com.lifestyle.viewmodels.UserViewModel

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var cardViewProfile: CardView
    lateinit var cardViewBmi: CardView
    lateinit var cardViewHiking: CardView
    lateinit var cardViewWeather: CardView
    lateinit var cardViewSettings: CardView
    lateinit var cardViewLogout: CardView
    lateinit var imageViewProfilePicture: ImageView
    lateinit var textViewMyDashboard: TextView

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.hide()

        // populate late init
        cardViewProfile = findViewById(R.id.cardViewProfile)
        cardViewBmi = findViewById(R.id.cardViewBmi)
        cardViewHiking = findViewById(R.id.cardViewHiking)
        cardViewWeather = findViewById(R.id.cardViewWeather)
        cardViewSettings = findViewById(R.id.cardViewSettings)
        cardViewLogout = findViewById(R.id.cardViewLogout)
        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture)
        textViewMyDashboard = findViewById(R.id.textViewMyDashboard)

        // bind observers from view models
        bindObservers()

        // bind listeners
        cardViewProfile.setOnClickListener(this)
        cardViewBmi.setOnClickListener(this)
        cardViewHiking.setOnClickListener(this)
        cardViewWeather.setOnClickListener(this)
        cardViewSettings.setOnClickListener(this)
        cardViewLogout.setOnClickListener(this)
    }

    private fun bindObservers() {
        // user data changed
        userViewModel.loggedInUser.observe(this) {
            println("(HomeActivity) Observer callback for: userLiveData")
             fillWithUserData()
        }
    }

    private fun fillWithUserData() {
        val user = userViewModel.loggedInUser.value
        if (user != null) {
            // set full name
            if (user.fullName != null) {
                textViewMyDashboard.text = "Welcome, ${user.fullName}"
            }

            // set custom pfp
            if (user.pictureURI != null) {
                imageViewProfilePicture.setImageURI(user.pictureURI!!.toUri())
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cardViewProfile -> {
                if (!userViewModel.isLoggedIn()) {
                    Toast.makeText(this, "User must be logged in", Toast.LENGTH_SHORT).show()
                    return
                }

                startActivity(Intent(this, ProfileActivity::class.java))
            }

            R.id.cardViewBmi -> {
                startActivity(Intent(this, FitnessActivity::class.java))
            }

            R.id.cardViewHiking -> {
                startActivity(Intent(this, HikingActivity::class.java))
            }

            R.id.cardViewWeather -> {
                if (!userViewModel.isLoggedIn()) {
                    Toast.makeText(this, "User must be logged in", Toast.LENGTH_SHORT).show()
                    return
                }
                startActivity(Intent(this, WeatherActivity::class.java))
            }

            R.id.cardViewSettings -> {
                startActivity(Intent(this, StepCounterActivity::class.java))
            }

            R.id.cardViewLogout -> {
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()

                userViewModel.logout()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}