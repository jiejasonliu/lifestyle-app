package com.lifestyle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.lifestyle.fragments.EditProfileFragment

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

                if (signupFragment == null) {
                    Toast.makeText(this, "Waiting for form to load...", Toast.LENGTH_SHORT).show()
                }

                val result = signupFragment.aggregateFields()
                val error = result?.firstError ?: "<no error>"
                Toast.makeText(this, result?.firstError, Toast.LENGTH_SHORT).show()
            }
        }
    }
}