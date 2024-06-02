package com.example.runtracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.runtracker.converters.LocalDateTimeConverter
import com.example.runtracker.converters.PointsListConverter

/**
 * The Room database for this app, containing the Run and Pin entities.
 */
@Database(entities = [Run::class, Pin::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateTimeConverter::class, PointsListConverter::class)
abstract class RunDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao // Data access object

    companion object {
        @Volatile
        private var INSTANCE: RunDatabase? = null

        /**
         * Get the singleton instance of the database.
         *
         * @param context The application context
         * @return The singleton instance of RunDatabase
         */
        fun getDatabase(context: Context): RunDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RunDatabase::class.java,
                    "run_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
