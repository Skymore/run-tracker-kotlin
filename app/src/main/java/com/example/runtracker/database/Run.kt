package com.example.runtracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime

/**
 * Represents a run in the database.
 */
@Entity
data class Run(
    @PrimaryKey(autoGenerate = true) val id: Int, // Primary key
    @ColumnInfo(name = "dateTime") val dateTime: LocalDateTime, // Date and time of start
    @ColumnInfo(name = "distance") val distance: Float, // Recorded distance in kilometers
    @ColumnInfo(name = "duration") val duration: Int, // Recorded duration in seconds
    @ColumnInfo(name = "points") val points: MutableList<GeoPoint>, // Points in recorded path
    @ColumnInfo(name = "calories") val calories: Float // Number of calories burned
)
