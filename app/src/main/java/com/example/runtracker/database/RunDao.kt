package com.example.runtracker.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for the Run database.
 */
@Dao
interface RunDao {

    @Query("SELECT * FROM run")
    fun getAll(): Flow<List<Run>> // Get all runs

    @Query("SELECT * FROM run WHERE dateTime == :dateTime")
    fun getByDate(dateTime: LocalDateTime): Flow<List<Run>> // Get runs by date

    @Query("SELECT * FROM run WHERE id == :runID")
    fun getByID(runID: Int): Flow<Run> // Get run by ID

    @Query("SELECT COUNT(*) FROM run")
    fun getNumberOfRuns(): Flow<Int> // Get total number of runs

    @Query("SELECT SUM(distance) FROM run")
    fun getTotalDistance(): Flow<Float> // Get total distance of all runs

    @Query("SELECT SUM(duration) FROM run")
    fun getTotalDuration(): Flow<Int> // Get total duration of all runs

    @Query("SELECT MAX(distance) FROM run")
    fun getLongestDistance(): Flow<Float> // Get the longest distance run

    @Query("SELECT MAX(duration) FROM run")
    fun getLongestDuration(): Flow<Int> // Get the longest duration run

    @Insert
    fun insert(run: Run) // Insert a single run

    @Insert
    fun insertAll(vararg runs: Run) // Insert multiple runs

    @Delete
    fun delete(run: Run) // Delete a run

    @Query("DELETE FROM run WHERE id = :runID")
    fun deleteByID(runID: Int) // Delete a run by ID

    @Query("DELETE FROM pin WHERE id = :runID")
    fun deletePinByID(runID: Int) // Delete a pin by ID

    @Query("DELETE FROM run")
    fun deleteAll() // Delete all runs

    @Update
    fun update(run: Run) // Update a run

    @Query("SELECT MAX(id) FROM run")
    fun getMaxRunID(): Flow<Int> // Get the highest run ID

    @Insert
    fun insertPin(pin: Pin) // Insert a pin

    @Query("SELECT MAX(calories) FROM run")
    fun getMaxCalories(): Flow<Float> // Get the maximum calories burned in a run

    @Query("SELECT SUM(calories) FROM run")
    fun getTotalCalories(): Flow<Float> // Get total calories burned

    @Query("SELECT * FROM Pin WHERE runId ==:runID")
    fun getPins(runID: Int): Flow<List<Pin>> // Get pins by run ID

    @Query("SELECT * FROM pin")
    fun getAllPins(): Flow<List<Pin>> // Get all pins

    @Query("SELECT COUNT(*) FROM pin")
    fun getNumberOfPins(): Flow<Int> // Get total number of pins
}
