package com.lifestyle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.lifestyle.viewmodels.UserViewModel

class WeatherActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        supportActionBar?.hide()

        // bind observers from view models
        bindObservers()
    }

    private fun bindObservers() {
        // user data changed
        userViewModel.loggedInUser.observe(this) {
            println("(WeatherActivity) Observer callback for: userViewModel.loggedInUser")

            // user was NOT logged in
            if (userViewModel.loggedInUser == null) {
                Toast.makeText(this, "User session expired", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}