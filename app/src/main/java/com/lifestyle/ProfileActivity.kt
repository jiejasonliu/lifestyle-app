package com.lifestyle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.lifestyle.fragments.EditProfileFragment
import com.lifestyle.models.LoginSession
import com.lifestyle.models.StoredUser

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var applyChangesButton: Button
    private lateinit var editProfileFragment: EditProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // hidden to add space for signup fields
        supportActionBar?.hide()

        // populate late init
        applyChangesButton = findViewById(R.id.buttonApplyChanges)
        editProfileFragment = supportFragmentManager.findFragmentById(R.id.fragmentEditProfile) as EditProfileFragment

        // register listeners
        applyChangesButton.setOnClickListener(this)

        // setup form
        val loginSession = LoginSession.getInstance(this)
        if (!loginSession.isLoggedIn()) {
            Toast.makeText(this, "User session expired", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
        editProfileFragment.disableImmutableFields()   // e.g. username
        editProfileFragment.fillProfileFields(loginSession.getLoggedInUser()!!)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonApplyChanges -> {
                val result = editProfileFragment.aggregateFieldsAndWrite()

                if (result == null) {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    return
                }

                if (!result.success || result.userProfile == null) {
                    Toast.makeText(this, result.firstError, Toast.LENGTH_SHORT).show()
                    return
                }

                Toast.makeText(this, "Changes were applied!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}