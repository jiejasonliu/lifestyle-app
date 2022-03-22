package com.lifestyle

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.lifestyle.models.PartialUserProfile
import com.lifestyle.models.UserProfileEntity
import com.lifestyle.viewmodels.UserViewModel

class FitnessActivity : AppCompatActivity(), View.OnClickListener {

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

    private var usersSex: String = "M" // Default to male
    private var usersAge: Int = 20 // Default to 20 years old

    private var weightChangeWatcherRegistered = false
    private var suppressWeightChangeWatcher = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi)

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
                fillWithUserData()
            }
            // user was NOT logged in
            else {
                Toast.makeText(this, "User session expired", Toast.LENGTH_SHORT).show()
            }

            // Register change listeners
            if (!weightChangeWatcherRegistered) {
                editTextWeightChange.addTextChangedListener(weightChangeTextWatcher);
                weightChangeWatcherRegistered = true
            }
        }
    }

    private fun fillWithUserData() {
        suppressWeightChangeWatcher = true

        // reset all buttons
        buttonLoseWeight.isSelected = false
        buttonMaintainWeight.isSelected = false
        buttonGainWeight.isSelected = false

        // Set height and weight from user's info
        val user = userViewModel.loggedInUser.value
        if(user != null) {
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
                    updateCalculations(false)   // only update UI not user
            }
        }

        suppressWeightChangeWatcher = false
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonUpdate -> {
                updateCalculations(true)
            }
            R.id.buttonLoseWeight -> {
                buttonLoseWeight.isSelected = true
                buttonMaintainWeight.isSelected = false
                buttonGainWeight.isSelected = false
                textViewLoseOrGain.setText("I want to lose")
                updateCalculations(false)
            }
            R.id.buttonMaintainWeight -> {
                buttonLoseWeight.isSelected = false
                buttonMaintainWeight.isSelected = true
                buttonGainWeight.isSelected = false
                editTextWeightChange.setText("0")
                updateCalculations(false)
            }
            R.id.buttonGainWeight -> {
                buttonLoseWeight.isSelected = false
                buttonMaintainWeight.isSelected = false
                buttonGainWeight.isSelected = true
                textViewLoseOrGain.setText("I want to gain")
                updateCalculations(false)
            }
            R.id.buttonActive -> {
                buttonActive.isSelected = true
                buttonNotActive.isSelected = false
                updateCalculations(false)
            }
            R.id.buttonNotActive -> {
                buttonActive.isSelected = false
                buttonNotActive.isSelected = true
                updateCalculations(false)
            }
        }
    }

    // @param updateUserProfile - true if we should take fields and update user in DB
    fun updateCalculations(shouldUpdateUserProfile: Boolean)
    {
        if(textInputWeight.text.toString() == "") {
            showErrorNullValue("weight")
            return
        }

        if(textInputHeightFt.text.toString() == "") {
            showErrorNullValue("height ft")
            return
        }

        if(textInputHeightIn.text.toString() == "") {
            showErrorNullValue("height in")
            return
        }

        val isActive = buttonActive.isSelected
        val weight : Int = textInputWeight.text.toString().toInt()
        val heightFt = textInputHeightFt.text.toString().toInt()
        val heightIn = textInputHeightIn.text.toString().toInt()
        var weightChange = editTextWeightChange.text.toString().toInt()

        if(weightChange > 2) {
            Toast.makeText(this, "You shouldn't try and change weight by more than 2 pounds in a week.", Toast.LENGTH_LONG).show()
        }

        // Update user profile
        val user = userViewModel.loggedInUser.value
        if(user != null && shouldUpdateUserProfile) {
            val weight : Int = textInputWeight.text.toString().toInt()
            val heightFt = textInputHeightFt.text.toString().toInt()
            val heightIn = textInputHeightIn.text.toString().toInt()
            var weightChange = editTextWeightChange.text.toString().toInt()
            updateUser(user, weight, heightFt, heightIn, weightChange)
        }

        if(buttonLoseWeight.isSelected)
            weightChange *= -1

        if(usersSex.equals("F"))
            updateCaloriesForFemale(weight, heightFt, heightIn, usersAge, weightChange, isActive)
        else
            updateCaloriesForMale(weight, heightFt, heightIn, usersAge, weightChange, isActive)
    }

    fun updateUser(user: UserProfileEntity, weight: Int, heightFt:Int, heightIn: Int, weightChange: Int) {
        var height: Int? = null
        if (heightFt != null && heightIn != null) {
            height = (12 * heightFt) + heightIn
        }

        userViewModel.updateUserProfilePartial(PartialUserProfile(user.username).apply {
            this.weight = weight
            this.height = height
            this.weightChange = weightChange
        })
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


    val weightChangeTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(p0: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (suppressWeightChangeWatcher) {
                return
            }

            if (s != null && s.isNotEmpty()) {
                if(buttonMaintainWeight.isSelected) {
                    Toast.makeText(this@FitnessActivity, "Select gain or lose weight", Toast.LENGTH_SHORT).show()
                }
                updateCalculations(true)
            }
        }
    }
}