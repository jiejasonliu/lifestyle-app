package com.lifestyle

import android.content.Context
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

        // todo: remove this once it's implemented
        val passwordTextView: EditText = findViewById(R.id.editTextTextPassword)
        passwordTextView.isFocusable = false

        // login and redirect if a previous login was found
        val loginSession = LoginSession.getInstance(this)
        if (loginSession.isLoggedIn() && loginSession.getLoggedInUser() != null) {
            finishAndLogin(loginSession.getLoggedInUser()!!.username)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonLogin -> {
                val username = username.text.toString()

                // todo: includes password in the future
                if (username.isNullOrEmpty()) {
                    Toast.makeText(this, "Please complete all the fields", Toast.LENGTH_SHORT).show()
                    return
                }

                // todo: hacky way to login since we don't have authentication yet
                // check if "username" field for that username is filled, otherwise this wasn't a user
                val candidateUser = this.getSharedPreferences(username, Context.MODE_PRIVATE)
                if (!candidateUser.contains("username")) {
                    Toast.makeText(this, "No profile was found with that username", Toast.LENGTH_SHORT).show()
                    return
                }

                finishAndLogin(username)
            }

            R.id.buttonSignup -> {
                startActivity(Intent(this, SignupActivity::class.java))
            }

            R.id.devButtonHome -> {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }

    // login, pop MainActivity from back stack, and redirect to HomeActivity
    private fun finishAndLogin(username: String) {
        LoginSession.getInstance(this).login(username)
        finish()
        startActivity(Intent(this, HomeActivity::class.java))
    }
}