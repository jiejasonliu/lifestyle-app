package com.lifestyle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class SignupActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var changePictureButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // hidden to add space for signup fields
        supportActionBar?.hide()

        // populate late init
        changePictureButton = findViewById(R.id.buttonChangePicture)

        // register listeners
        changePictureButton.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonChangePicture -> {
                // todo: actually hook up and save picture to URI
                Toast.makeText(this, "Change Picture Clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }
}