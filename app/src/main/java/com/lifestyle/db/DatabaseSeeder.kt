package com.lifestyle.db

import android.content.Context
import com.lifestyle.models.SettingsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseSeeder {

    companion object {
        fun seed(appContext: Context) {
            println("Seeding database...")

            val db = LifestyleDatabase.getInstance(appContext)

            // default settings
            db.settingsDao().insert(SettingsEntity("loggedInUser", "#LOGGED-OUT#"))
        }
    }
}