package com.lifestyle.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lifestyle.models.SettingsEntity
import com.lifestyle.models.UserProfileEntity
import java.util.concurrent.Executors

@Database(entities = [UserProfileEntity::class, SettingsEntity::class], version = 1)
abstract class LifestyleDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun settingsDao(): SettingsDao

    companion object Factory {
        @Volatile private var instance: LifestyleDatabase? = null

        // database is created lazily -- only when one of the Dao methods are called
        // in Android Studio, you can view the DB by View -> Tool Windows -> App Inspection
        fun getInstance(context: Context): LifestyleDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): LifestyleDatabase {
            return Room.databaseBuilder(context, LifestyleDatabase::class.java, "lifestyle.db")
                .allowMainThreadQueries()   // we are OK with freezing the UI during times like checking if username was taken
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Executors.newSingleThreadExecutor().execute {
                            DatabaseSeeder.seed(context)
                        }
                    }
                })
            .build()
        }
    }
}