package com.lifestyle

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.lifestyle.models.LoginSession
import com.lifestyle.models.StoredUser
import com.lifestyle.viewmodels.ProfileFormViewModel
import com.lifestyle.viewmodels.UserViewModel
import java.text.DecimalFormat

class FitnessActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener {

    private lateinit var textInputWeight: TextInputEditText
    private lateinit var textInputHeightFt: TextInputEditText
    private lateinit var textInputHeightIn: TextInputEditText
    private lateinit var buttonUpdate: Button
    private lateinit var buttonLoseWeight: Button
    private lateinit var buttonMaintainWeight: Button
    private lateinit var buttonGainWeight: Button
    private lateinit var buttonActive: Button
    private lateinit var buttonNotActive: Button
    private lateinit var textViewLoseOrGain : TextView
    private lateinit var editTextWeightChange: EditText
    private lateinit var textViewBMI: TextView
    private lateinit var textViewBMR: TextView
    private lateinit var textViewTargetCalories: TextView

    private val userViewModel: UserViewModel by viewModels()
    private val profileFormViewModel: ProfileFormViewModel by viewModels()

    private var optionalUser: StoredUser? = null    // initialized in onCreate
    private var usersSex: String = "M" // Default to male
    private var usersAge: Int = 20 // Default to 20 years old

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi)

        optionalUser = LoginSession.getInstance(this).getLoggedInUser()

        // Gather view objects
        textInputWeight = findViewById(R.id.textInputEditTextWeight)
        textInputHeightFt = findViewById(R.id.textInputEditTextHeightFt)
        textInputHeightIn = findViewById(R.id.textInputEditTextHeightIn)
        buttonUpdate = findViewById(R.id.buttonUpdate)
        buttonLoseWeight = findViewById(R.id.buttonLoseWeight)
        buttonMaintainWeight = findViewById(R.id.buttonMaintainWeight)
        buttonGainWeight = findViewById(R.id.buttonGainWeight)
        buttonActive = findViewById(R.id.buttonActive)
        buttonNotActive = findViewById(R.id.buttonNotActive)
        textViewLoseOrGain = findViewById(R.id.textViewLoseOrGain)
        editTextWeightChange = findViewById(R.id.editTextWeightChange)
        textViewBMI = findViewById(R.id.textViewBMI)
        textViewBMR = findViewById(R.id.textViewBMR)
        textViewTargetCalories = findViewById(R.id.textViewTargetCalories)

        // Register click listener
        buttonUpdate.setOnClickListener(this)
        buttonLoseWeight.setOnClickListener(this)
        buttonGainWeight.setOnClickListener(this)
        buttonMaintainWeight.setOnClickListener(this)
        buttonActive.setOnClickListener(this)
        buttonNotActive.setOnClickListener(this)

        // Register change listeners
        // I couldnt get this to work
        // Values wont update when they change their "weight change" goal
        editTextWeightChange.setOnFocusChangeListener(this);

        // bind observers from view models
        bindObservers()

        // Defaults to "Is Active" setting
        buttonActive.isSelected = true
    }

    private fun bindObservers() {
        // user data changed
        userViewModel.loggedInUser.observe(this) {
            println("(FitnessActivity) Observer callback for: userViewModel.loggedInUser")

            // check if a user is logged in
            if (userViewModel.loggedInUser != null) {
                profileFormViewModel.updateFormFull(userViewModel.loggedInUser.value!!)
            }
            // user was NOT logged in
            else {
                Toast.makeText(this, "User session expired", Toast.LENGTH_SHORT).show()
            }
            profileFormViewModel.updateFormFull(userViewModel.loggedInUser.value!!)
            fillWithUserData()
        }

        // form validation occurred
        profileFormViewModel.validationResult.observe(this) {
            println("(FitnessActivity) Observer callback for: profileFormViewModel.validationResult")

            val result = profileFormViewModel.validationResult.value ?: return@observe

            // error when validating
            if (!result.success || result.partialUserProfile == null) {
                Toast.makeText(this, result.firstError, Toast.LENGTH_SHORT).show()
            }
            // success! apply changes to UserViewModel
            else {
                userViewModel.updateUserProfilePartial(result.partialUserProfile)
                Toast.makeText(this, "Changes were applied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fillWithUserData() {
        // Set height and weight from user's info
        if(optionalUser != null) {
            val user = userViewModel.loggedInUser.value

            if (!user?.sex.isNullOrBlank())
                usersSex = user?.sex!!

            if (user?.age != null)
                usersAge = user.age!!

            if (user?.weight != null)
                textInputWeight.setText(user.weight!!.toString())

            if (user?.weightChange != null) {
                val weightChange = user.weightChange!!
                if (weightChange < 0) {
                    editTextWeightChange.setText((weightChange * -1).toString())
                    buttonLoseWeight.isSelected = true
                    textViewLoseOrGain.setText("I want to lose")
                } else if (weightChange > 0) {
                    editTextWeightChange.setText(weightChange.toString())
                    buttonGainWeight.isSelected = true
                    textViewLoseOrGain.setText("I want to gain")
                } else {
                    buttonMaintainWeight.isSelected = true
                }
            } else {
                buttonMaintainWeight.isSelected = true
            }

            if (user?.height != null) {
                val feet = user.height?.div(12)
                val inches = user.height?.mod(12)

                textInputHeightFt.setText(feet.toString())
                textInputHeightIn.setText(inches.toString())

                // Set calculations if they have weight and height
                if (user.weight != null)
                    updateCalculations()
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonUpdate -> {
                profileFormViewModel.validateFormFields()   // -> validationResult.observe callback (see in bindObservers())
                updateCalculations()
            }
            R.id.buttonLoseWeight -> {
                buttonLoseWeight.isSelected = true
                buttonMaintainWeight.isSelected = false
                buttonGainWeight.isSelected = false
                textViewLoseOrGain.setText("I want to lose")
                updateCalculations()
            }
            R.id.buttonMaintainWeight -> {
                buttonLoseWeight.isSelected = false
                buttonMaintainWeight.isSelected = true
                buttonGainWeight.isSelected = false
                editTextWeightChange.setText("0")
                updateCalculations()
            }
            R.id.buttonGainWeight -> {
                buttonLoseWeight.isSelected = false
                buttonMaintainWeight.isSelected = false
                buttonGainWeight.isSelected = true
                textViewLoseOrGain.setText("I want to gain")
                updateCalculations()
            }
            R.id.buttonActive -> {
                buttonActive.isSelected = true
                buttonNotActive.isSelected = false
                updateCalculations()
            }
            R.id.buttonNotActive -> {
                buttonActive.isSelected = false
                buttonNotActive.isSelected = true
                updateCalculations()
            }
        }
    }

    fun updateCalculations()
    {
        if(textInputWeight.text.toString() == "") {
            showErrorNullValue("weight")
            return
        }
        val weight : Int = textInputWeight.text.toString().toInt()

        if(textInputHeightFt.text.toString() == "") {
            showErrorNullValue("height ft")
            return
        }
        val heightFt = textInputHeightFt.text.toString().toInt()

        if(textInputHeightIn.text.toString() == "") {
            showErrorNullValue("height in")
            return
        }
        val heightIn = textInputHeightIn.text.toString().toInt()

        val isActive = buttonActive.isSelected
        var weightChange = editTextWeightChange.text.toString().toInt()

        if(weightChange > 2) {
            Toast.makeText(this, "You shouldn't try and change weight by more than 2 pounds in a week.", Toast.LENGTH_LONG).show()
        }

        if(buttonLoseWeight.isSelected)
            weightChange *= -1

        // Update user profile
        if(optionalUser != null) {
            val user = StoredUser(this, optionalUser?.username.toString())
            updateUser(user, weight, heightFt, heightIn, weightChange)
        }

        if(usersSex.equals("F"))
            updateCaloriesForFemale(weight, heightFt, heightIn, usersAge, weightChange, isActive)
        else
            updateCaloriesForMale(weight, heightFt, heightIn, usersAge, weightChange, isActive)
    }

    fun updateUser(user: StoredUser, weight: Int, heightFt:Int, heightIn: Int, weightChange: Int) {
        var height: Int? = null
        if (heightFt != null && heightIn != null) {
            height = (12 * heightFt) + heightIn
        }

        user.weight = weight
        user.height = height
        user.weightChange = weightChange
    }

    fun updateCaloriesForMale(weight: Int, heightFt: Int, heightIn: Int, age: Int, weightChange: Int, isActive: Boolean) {
        val weightInKG = .453592 * weight
        val heightInInches = (heightFt * 12) + heightIn
        val heightInCM = 2.54 * heightInInches
        var bmi = (weight.toDouble() / (heightInInches.toDouble() * heightInInches.toDouble())) * 703.0
        val bmr = (10 * weightInKG) + (6.25 * heightInCM) - (5 * age) + 5

        var caloriesToMakeDifference = 0
        if(weightChange != 0)
            caloriesToMakeDifference = weightChange * 3500 / 7

        textViewBMI.setText(String.format("%.1f", bmi))

        if(isActive) {
            var newBMR = (bmr * 1.55).toInt()
            textViewBMR.setText(newBMR.toString())
            textViewTargetCalories.setText((newBMR + caloriesToMakeDifference).toString())
            if((newBMR + caloriesToMakeDifference) < 1200)
                Toast.makeText(this, "You should be eating more than 1200 calories", Toast.LENGTH_SHORT).show()
        }
        else {
            val newBMR = (bmr * 1.2).toInt()
            textViewBMR.setText(newBMR.toString())
            textViewTargetCalories.setText((newBMR + caloriesToMakeDifference).toString())
            if((newBMR + caloriesToMakeDifference) < 1200)
                Toast.makeText(this, "You should be eating more than 1200 calories", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateCaloriesForFemale(weight: Int, heightFt: Int, heightIn: Int, age: Int, weightChange: Int, isActive: Boolean) {
        val weightInKG = .453592 * weight
        val heightInInches = (heightFt * 12) + heightIn
        val heightInCM = 2.54 * heightInInches
        val bmi = (weight / (heightInInches * heightInInches)) * 703
        var bmr = (10 * weightInKG) + (6.25 * heightInCM) - (5 * age) - 161
        val caloriesToMakeDifference = weightChange * 3500 / 7

        textViewBMI.setText(String.format("%.1f", bmi))

        if(isActive) {
            val newBMR = (bmr * 1.55).toInt()
            textViewBMR.setText(newBMR.toString())
            textViewTargetCalories.setText((newBMR + caloriesToMakeDifference).toString())
            if((newBMR + caloriesToMakeDifference) < 1000)
                Toast.makeText(this, "You should be eating more than 1000 calories", Toast.LENGTH_SHORT).show()
        }
        else {
            val newBMR = (bmr * 1.2).toInt()
            textViewBMR.setText(newBMR.toString())
            textViewTargetCalories.setText((newBMR + caloriesToMakeDifference).toString())
            if((newBMR + caloriesToMakeDifference) < 1000)
                Toast.makeText(this, "You should be eating more than 1000 calories", Toast.LENGTH_SHORT).show()
        }
    }

    fun showErrorNullValue(value: String)
    {
        Toast.makeText(this, "Please fill in the " + value + " field.", Toast.LENGTH_SHORT).show()
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if(!hasFocus) {
            when (view?.id) {
                R.id.editTextWeightChange -> {
                    if(editTextWeightChange.text.toString() != "0") {
                        if(buttonMaintainWeight.isSelected)
                            Toast.makeText(this, "Select gain or lose weight", Toast.LENGTH_SHORT).show()
                        else
                            updateCalculations()
                    } else
                        updateCalculations()
                }
            }
        }
    }
}