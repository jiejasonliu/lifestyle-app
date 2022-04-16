package com.lifestyle

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lifestyle.models.PartialUserProfile
import com.lifestyle.models.UserProfileEntity
import com.lifestyle.viewmodels.UserViewModel
import java.time.LocalDateTime

class StepCounterActivity : AppCompatActivity() {

    private var progr = 0
    private lateinit var textViewTodaysSteps: TextView
    private lateinit var textViewTotalSteps: TextView
    private lateinit var editTextStepGoal: EditText
    private lateinit var buttonUpdateStepGoal: Button
    private lateinit var buttonClearStepData: Button
    private lateinit var progressBar: ProgressBar

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)

        textViewTodaysSteps = findViewById(R.id.textViewTodaysSteps)
        textViewTotalSteps = findViewById(R.id.textViewTotalSteps)
        editTextStepGoal = findViewById(R.id.editTextStepGoal)
        buttonUpdateStepGoal = findViewById(R.id.buttonUpdateStepGoal)
        buttonClearStepData = findViewById(R.id.buttonClearStepData)
        progressBar = findViewById(R.id.progress_bar)

        bindObservers()


        buttonUpdateStepGoal.setOnClickListener {
            val user = userViewModel.loggedInUser.value
            if(user != null && editTextStepGoal.text?.toString() != null) {
                updateUserData(user, editTextStepGoal.text.toString().toInt(), user.totalSteps.toString().toInt(), user.todaysSteps.toString().toInt(), user.dateOfTodaysSteps.toString().toInt())
                updateProgressBar(user.todaysSteps.toString().toInt(), editTextStepGoal.text.toString().toInt())
            }
        }

        buttonClearStepData.setOnClickListener {
            val user = userViewModel.loggedInUser.value
            if(user != null) {
                updateUserData(user, user.stepGoal.toString().toInt(), 0, 0, user.dateOfTodaysSteps.toString().toInt())
                updateProgressBar(0, user.stepGoal.toString().toInt())
            }
        }
    }

    private fun bindObservers() {
        // user data changed
        userViewModel.loggedInUser.observe(this) {
            println("(StepCounterActivity) Observer callback for: userLiveData")
            fillWithUserData()
        }
    }

    private fun fillWithUserData() {
        val user = userViewModel.loggedInUser.value
        if (user != null) {

            // Add data if it doesnt exist
            if(user.stepGoal == null || user.stepGoal == 0) {
                updateUserData(user,3000, 0, 0, LocalDateTime.now().dayOfMonth);

                textViewTodaysSteps.text = "0"
                textViewTotalSteps.text = "0"
                editTextStepGoal.setText("3000")

                updateProgressBar(0, 3000)
            }

            // Check if we need to reset todays steps
            else if(user.dateOfTodaysSteps != LocalDateTime.now().dayOfMonth)
            {
                updateUserData(user, user.stepGoal.toString().toInt(), user.totalSteps.toString().toInt(), 0, LocalDateTime.now().dayOfMonth);

                textViewTodaysSteps.text = "0"
                textViewTotalSteps.text = user.totalSteps.toString()
                editTextStepGoal.setText(user.stepGoal.toString())

                updateProgressBar(0, user.stepGoal.toString().toInt())
            }

            // set ui from user data
            else if (user.totalSteps != null) {
                textViewTodaysSteps.text = "${user.todaysSteps}"
                textViewTotalSteps.text = "${user.totalSteps}"
                editTextStepGoal.setText("${user.stepGoal}")

                updateProgressBar(user.todaysSteps.toString().toInt(), user.stepGoal.toString().toInt())
            }

        }
    }

    private fun updateUserData(user: UserProfileEntity, stepGoal:Int, totalSteps: Int, todaysSteps:Int, dateOfTodaysSteps:Int) {
        userViewModel.updateUserProfilePartial(PartialUserProfile(user.username).apply {
            this.stepGoal = stepGoal
            this.totalSteps = totalSteps
            this.todaysSteps = todaysSteps
            this.dateOfTodaysSteps = dateOfTodaysSteps
        })
    }

    private fun updateProgressBar(todaysSteps:Int, stepGoal: Int) {

        if(todaysSteps == 0) {
            progressBar.progress = 0
        }
        else if(stepGoal == 0 || todaysSteps > stepGoal)
        {
            progressBar.progress = 100
        }
        else {
            progressBar.progress = todaysSteps / stepGoal
        }

    }
}