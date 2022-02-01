package com.lifestyle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.lifestyle.fragments.EditProfileFragment
import com.lifestyle.models.LoginSession

class SignupActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var changePictureButton: Button
    private lateinit var finishSignupButton: Button
    private lateinit var signupFragment: EditProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // hidden to add space for signup fields
        supportActionBar?.hide()

        // populate late init
        changePictureButton = findViewById(R.id.buttonChangePicture)
        finishSignupButton = findViewById(R.id.buttonFinishSignup)

        // register listeners
        changePictureButton.setOnClickListener(this)
        finishSignupButton.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonChangePicture -> {
                // todo: actually hook up and save picture to URI
                Toast.makeText(this, "Change Picture Clicked", Toast.LENGTH_SHORT).show()
            }

            R.id.buttonFinishSignup -> {
                signupFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentEditProfileSignup) as EditProfileFragment

                // pull in data from fields and write if successful
                val result = signupFragment.aggregateFieldsAndWrite()

                if (result == null) {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    return
                }

                if (!result.success || result.userProfile == null) {
                    Toast.makeText(this, result.firstError, Toast.LENGTH_SHORT).show()
                    return
                }

                // successful
                LoginSession.getInstance(applicationContext).login(result.userProfile.username)
                finishSignupButton.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    finishSignupButton.isEnabled = true
                }, 250)
            }
        }
    }
}