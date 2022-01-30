package com.lifestyle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.view.View
import com.lifestyle.models.LoginSession


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button
    private lateinit var devHomeButton: Button
    private lateinit var username: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // populate late init
        username = findViewById(R.id.editTextTextUsername)
        password = findViewById(R.id.editTextTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        signupButton = findViewById(R.id.buttonSignup)
        devHomeButton = findViewById(R.id.devButtonHome)

        // register listeners
        loginButton.setOnClickListener(this)
        signupButton.setOnClickListener(this)
        devHomeButton.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonLogin -> {
                if (username.text.isNullOrEmpty() || password.text.isNullOrEmpty()) {
                    Toast.makeText(this, "Please complete all the fields", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.buttonSignup -> {
                startActivity(Intent(this, SignupActivity::class.java))
            }

            R.id.devButtonHome -> {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }
}