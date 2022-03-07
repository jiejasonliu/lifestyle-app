package com.lifestyle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.lifestyle.fragments.WeatherFragment
import com.lifestyle.models.LoginSession
import com.lifestyle.models.StoredUser

class WeatherActivity : AppCompatActivity() {
    private lateinit var weatherFragment: WeatherFragment
    private var user: StoredUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        supportActionBar?.hide()

        user = LoginSession.getInstance(this).getLoggedInUser()

        if (savedInstanceState == null) {
            if (user == null) {
                finish()
                return
            }
        }
    }
}