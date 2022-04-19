package com.lifestyle.db

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.impl.WorkDatabasePathHelper.getDatabasePath
import com.amplifyframework.core.Amplify
import java.io.File
import java.util.*

class UploadBackupWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        uploadDbBackup(inputData.getString("main"), inputData.getString("wal"), inputData.getString("shm"))
        return Result.success()
    }

    private fun uploadDbBackup(main: String?, wal: String?, shm: String?) {
        if (main == null || wal == null || shm == null) {
            Log.i("AmplifyApp", "One or more file path parameters were not included")
            return
        }

        val dbFileMain = File(main)
        val dbFileWal = File(wal)
        val dbFileShm = File(shm)

        if (dbFileMain == null || dbFileWal == null || dbFileShm == null) {
            Log.i("AmplifyApp", "One or more DB file paths not found")
            return
        }

        val time = Date().time;
        Amplify.Storage.uploadFile("lifestyle-backup-${time}.db", dbFileMain,
            { Log.i("AmplifyApp", "Successfully uploaded: ${it.key}") },
            { Log.e("AmplifyApp", "Upload failed", it) }
        )

        Amplify.Storage.uploadFile("lifestyle-backup-${time}.db-wal", dbFileWal,
            { Log.i("AmplifyApp", "Successfully uploaded: ${it.key}") },
            { Log.e("AmplifyApp", "Upload failed", it) }
        )

        Amplify.Storage.uploadFile("lifestyle-backup-${time}.db-shm", dbFileShm,
            { Log.i("AmplifyApp", "Successfully uploaded: ${it.key}") },
            { Log.e("AmplifyApp", "Upload failed", it) }
        )
    }
}