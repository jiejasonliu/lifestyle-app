package com.lifestyle

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
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
import android.hardware.SensorManager
import kotlin.math.abs
import kotlin.math.sqrt


class HomeActivity : AppCompatActivity(), View.OnClickListener, SensorEventListener {
    lateinit var cardViewProfile: CardView
    lateinit var cardViewBmi: CardView
    lateinit var cardViewHiking: CardView
    lateinit var cardViewWeather: CardView
    lateinit var cardViewSettings: CardView
    lateinit var cardViewLogout: CardView
    lateinit var imageViewProfilePicture: ImageView
    lateinit var textViewMyDashboard: TextView

    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private var accelerationCurrent: Double = 9.809989073394384 // gravity
    private var accelerationPrevious: Double = 9.809989073394384 // gravity
    private var accelerationLowCutFilter: Float = 0f
    private var shakeThreshold: Float = 8f


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

        // initialize sensors
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (mAccelerometer != null) {
            // Success! There's a ACCELEROMETER).
        } else {
            // Failure! No ACCELEROMETER).
        }
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


    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this);
    }

    override fun onSensorChanged(p0: SensorEvent) {
        val x = p0.values[0]
        val y = p0.values[1]
        val z = p0.values[2]

        // normalize calculate magnitude of vector
        accelerationCurrent = sqrt((x*x + y*y + z*z).toDouble())
        val accelerationDelta = abs(accelerationCurrent-accelerationPrevious).toFloat()
        accelerationLowCutFilter = accelerationLowCutFilter * 0.9f + accelerationDelta
        if (accelerationLowCutFilter > shakeThreshold) {
            Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show()
            // TODO: Start step counter intent
        }
        accelerationPrevious = accelerationCurrent
    }

    override fun onAccuracyChanged(p0: Sensor, p1: Int) {
    }

}