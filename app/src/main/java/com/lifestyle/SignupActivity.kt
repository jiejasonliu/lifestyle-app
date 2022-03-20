package com.lifestyle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import com.lifestyle.fragments.EditProfileFragment
import com.lifestyle.models.LoginSession
import com.lifestyle.viewmodels.ProfileFormViewModel
import com.lifestyle.viewmodels.UserViewModel

class SignupActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var finishSignupButton: Button
    lateinit var signupFragment: EditProfileFragment

    private val userViewModel: UserViewModel by viewModels()
    private val profileFormViewModel: ProfileFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // hidden to add space for signup fields
        supportActionBar?.hide()

        // populate late init
        finishSignupButton = findViewById(R.id.buttonFinishSignup)
        signupFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentEditProfileSignup) as EditProfileFragment

        // bind observers from view models
        bindObservers()

        // register listeners
        finishSignupButton.setOnClickListener(this)
    }

    private fun bindObservers() {
        // form validation occurred
        profileFormViewModel.validationResult.observe(this) {
            println("(SignupActivity) Observer callback for: profileFormViewModel.validationResult")

            val result = profileFormViewModel.validationResult.value ?: return@observe

            // error when validating
            if (!result.success || result.partialUserProfile == null) {
                Toast.makeText(this, result.firstError, Toast.LENGTH_SHORT).show()
            }
            // success! apply changes to UserViewModel, then login
            else {
                userViewModel.login(result.partialUserProfile.username)
                userViewModel.updateUserProfilePartial(result.partialUserProfile)

                finishSignupButton.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    finishSignupButton.isEnabled = true
                }, 250)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonFinishSignup -> {
                // check if user already exists before writing
                var candidateUsername = signupFragment.binding.textInputEditTextUsername.text.toString()
                if (candidateUsername != null && userViewModel.doesUserExist(candidateUsername)) {
                    Toast.makeText(this, "This username is already taken", Toast.LENGTH_SHORT).show()
                    return
                }

                // try to validate and create new account
                profileFormViewModel.validateFormFields()   // -> validationResult.observe callback (see in bindObservers())
            }
        }
    }
}