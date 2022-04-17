package com.lifestyle

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lifestyle.models.PartialUserProfile
import com.lifestyle.models.UserProfileEntity
import com.lifestyle.viewmodels.UserViewModel
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.sqrt

class StepCounterActivity : AppCompatActivity(), SensorEventListener {

    private var progr = 0
    private lateinit var textViewTodaysSteps: TextView
    private lateinit var textViewTotalSteps: TextView
    private lateinit var editTextStepGoal: EditText
    private lateinit var buttonUpdateStepGoal: Button
    private lateinit var buttonClearStepData: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private var accelerationCurrent: Double = 9.809989073394384 // gravity
    private var accelerationPrevious: Double = 9.809989073394384 // gravity
    private var accelerationLowCutFilter: Float = 0f
    private var shakeThreshold: Float = 8f
    private var timeThreshold: Long = 1000
    private var prevTime: Long = System.currentTimeMillis()
    private var shakeCount: Int = 0
    private var shakeCountThreshold: Int = 2

    private lateinit var mediaPlayer: MediaPlayer
    private var trackingSteps: Boolean = false

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

        // initialize sensors
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (mAccelerometer != null) {
            // Success! There's a ACCELEROMETER).
        } else {
            // Failure! No ACCELEROMETER).
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

    override fun onResume() {
        super.onResume()
        mSensorManager.flush(this)
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        shakeCount = 0
        accelerationCurrent = 9.809989073394384
        accelerationPrevious = 9.809989073394384
        accelerationLowCutFilter = 0.0f
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this);
    }

    override fun onSensorChanged(p0: SensorEvent) {
        var currTime = System.currentTimeMillis()

        if ((currTime - prevTime) > timeThreshold) {
            shakeCount += 1
            prevTime = currTime

            val x = p0.values[0]
            val y = p0.values[1]
            val z = p0.values[2]

            // normalize calculate magnitude of vector
            accelerationCurrent = sqrt((x*x + y*y + z*z).toDouble())
            val accelerationDelta = abs(accelerationCurrent-accelerationPrevious).toFloat()
            accelerationLowCutFilter = accelerationLowCutFilter * 0.9f + accelerationDelta
            accelerationPrevious = accelerationCurrent
            if (accelerationLowCutFilter > shakeThreshold && shakeCount > shakeCountThreshold) {

                if (!trackingSteps) {
                    accelerationCurrent = 9.809989073394384
                    accelerationPrevious = 9.809989073394384
                    accelerationLowCutFilter = 0.0f
                    mSensorManager.flush(this)

                    // TODO: Start tracking steps
                    Toast.makeText(this, "Step counter is now tracking steps", Toast.LENGTH_SHORT).show()

                    // initialize media player
                    mediaPlayer = MediaPlayer.create(this, R.raw.step_counter_start)
                    mediaPlayer.start()

                    trackingSteps = true
                } else {
                    accelerationCurrent = 9.809989073394384
                    accelerationPrevious = 9.809989073394384
                    accelerationLowCutFilter = 0.0f
                    mSensorManager.flush(this)

                    // TODO: Stop tracking steps
                    Toast.makeText(this, "Step counter has stopped tracking steps", Toast.LENGTH_SHORT).show()

                    // initialize media player
                    mediaPlayer = MediaPlayer.create(this, R.raw.step_counter_stop)
                    mediaPlayer.start()

                    trackingSteps = false
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor, p1: Int) {
    }
}