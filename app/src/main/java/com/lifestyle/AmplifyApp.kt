package com.lifestyle

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import com.lifestyle.db.LifestyleDatabase
import com.lifestyle.db.UploadBackupWorker
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AmplifyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)

            Log.i("AmplifyApp","Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.i("AmplifyApp", "Could not initialize Amplify", error)
        }

        val fileMap = mutableMapOf(
            "main" to getDatabasePath("lifestyle.db").absolutePath,
            "wal" to getDatabasePath("lifestyle.db-wal").absolutePath,
            "shm" to getDatabasePath("lifestyle.db-shm").absolutePath)

        // periodically save Room DB to S3 bucket
        val periodicWorkRequest = PeriodicWorkRequest.Builder(UploadBackupWorker::class.java, 15, TimeUnit.MINUTES)
            .setInputData(Data.Builder().putAll(fileMap as Map<String, Any>).build())
            .setInitialDelay(1, TimeUnit.SECONDS)   // save 1 sec after app is launched
            .build()

        val workManager = WorkManager.getInstance(this).apply {
            enqueue(periodicWorkRequest)
        }
    }
}