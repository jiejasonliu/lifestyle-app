package com.lifestyle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import com.lifestyle.fragments.EditProfileFragment
import com.lifestyle.viewmodels.*

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var applyChangesButton: Button
    lateinit var editProfileFragment: EditProfileFragment

    private val userViewModel: UserViewModel by viewModels()
    private val profileFormViewModel: ProfileFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // hidden to add space for signup fields
        supportActionBar?.hide()

        // populate late init
        applyChangesButton = findViewById(R.id.buttonApplyChanges)
        editProfileFragment = supportFragmentManager.findFragmentById(R.id.fragmentEditProfile) as EditProfileFragment

        // bind observers from view models
        bindObservers()

        // register listeners
        applyChangesButton.setOnClickListener(this)
    }

    private fun bindObservers() {
        // user data changed
        userViewModel.loggedInUser.observe(this) {
            println("(ProfileActivity) Observer callback for: userViewModel.loggedInUser")

            // check if a user is logged in
            if (userViewModel.loggedInUser != null) {
                editProfileFragment.disableImmutableFields()   // e.g. disable username field
                profileFormViewModel.updateFormFull(userViewModel.loggedInUser.value!!)
            }
            // user was NOT logged in
            else {
                Toast.makeText(this, "User session expired", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // form validation occurred
        profileFormViewModel.validationResult.observe(this) {
            println("(ProfileActivity) Observer callback for: profileFormViewModel.validationResult")

            val result = profileFormViewModel.validationResult.value ?: return@observe

            // error when validating
            if (!result.success || result.partialUserProfile == null) {
                Toast.makeText(this, result.firstError, Toast.LENGTH_SHORT).show()
            }
            // success! apply changes to UserViewModel
            else {
                userViewModel.updateUserProfilePartial(result.partialUserProfile)
                Toast.makeText(this, "Changes were applied!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonApplyChanges -> {
                // try to validate and update account
                profileFormViewModel.validateFormFields()   // -> validationResult.observe callback (see in bindObservers())
            }
        }
    }
}