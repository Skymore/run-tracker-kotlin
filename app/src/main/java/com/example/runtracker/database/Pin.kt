package com.example.runtracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.osmdroid.util.GeoPoint

/**
 * Represents a pin in the database.
 */
@Entity
data class Pin(
    @PrimaryKey(autoGenerate = true) val id: Int, // Primary key
    @ColumnInfo(name = "geoPoint") val geoPoint: GeoPoint, // Geographic point
    @ColumnInfo(name = "imagePath") val image_path: String, // Path to associated image
    @ColumnInfo(name = "runId") val runId: Int // Associated run ID
)
