package com.lifestyle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import com.lifestyle.models.LoginSession
import com.lifestyle.models.StoredUser

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var cardViewProfile: CardView
    private lateinit var cardViewBmi: CardView
    private lateinit var cardViewHiking: CardView
    private lateinit var cardViewWeather: CardView
    private lateinit var cardViewSettings: CardView
    private lateinit var cardViewLogout: CardView
    private lateinit var imageViewProfilePicture: ImageView
    private lateinit var textViewMyDashboard: TextView

    private var optionalUser: StoredUser? = null    // initialized in onCreate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        optionalUser = LoginSession.getInstance(this).getLoggedInUser()

        // populate late init
        cardViewProfile = findViewById(R.id.cardViewProfile)
        cardViewBmi = findViewById(R.id.cardViewBmi)
        cardViewHiking = findViewById(R.id.cardViewHiking)
        cardViewWeather = findViewById(R.id.cardViewWeather)
        cardViewSettings = findViewById(R.id.cardViewSettings)
        cardViewLogout = findViewById(R.id.cardViewLogout)
        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture)
        textViewMyDashboard = findViewById(R.id.textViewMyDashboard)

        // bind listeners
        cardViewProfile.setOnClickListener(this)
        cardViewBmi.setOnClickListener(this)
        cardViewHiking.setOnClickListener(this)
        cardViewWeather.setOnClickListener(this)
        cardViewSettings.setOnClickListener(this)
        cardViewLogout.setOnClickListener(this)

        // fill in optional data
        fillWithUserData()
    }

    /**
     * The HomeActivity can be changed on resume:
     *  (1) outside source affecting image files
     *  (2) from editing your profile (name, img, etc.)
     */
    override fun onResume() {
        super.onResume()
        fillWithUserData()  // refetch
    }

    private fun fillWithUserData() {
        if (optionalUser != null) {
            val user = optionalUser!!

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
                Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ProfileActivity::class.java))
            }

            R.id.cardViewBmi -> {
                Toast.makeText(this, "BMI Calculator Clicked", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, BMIActivity::class.java))
            }

            R.id.cardViewHiking -> {
                Toast.makeText(this, "Hiking Trails Clicked", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HikingActivity::class.java))
            }

            R.id.cardViewWeather -> {
                Toast.makeText(this, "Weather Clicked", Toast.LENGTH_SHORT).show()
            }

            R.id.cardViewSettings -> {
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show()
            }

            R.id.cardViewLogout -> {
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()

                LoginSession.getInstance(applicationContext).logout()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}