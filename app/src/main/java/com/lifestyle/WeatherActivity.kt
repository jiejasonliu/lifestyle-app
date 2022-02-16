package com.lifestyle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, WeatherFragmentActivity.newInstance())
                .commit()
        }
    }
}