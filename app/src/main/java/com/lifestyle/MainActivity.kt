package com.lifestyle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent


class MainActivity : AppCompatActivity() {
    private lateinit var loginButton : Button
    private lateinit var username : EditText
    private lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = findViewById(R.id.editTextTextUsername)
        password = findViewById(R.id.editTextTextPassword)

        loginButton = findViewById(R.id.buttonLogin)
        loginButton.setOnClickListener{
            if (username.text.isNullOrEmpty()||password.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please complete all the fields", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.putExtra("username", username.text.toString())
                startActivity(intent)
            }
        }
    }
}