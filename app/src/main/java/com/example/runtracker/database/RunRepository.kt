package com.example.runtracker.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository for managing Run and Pin data operations.
 */
class RunRepository(private val runDao: RunDao) {

    val allRuns: Flow<List<Run>> = runDao.getAll() // All runs
    val numberOfRuns: Flow<Int> = runDao.getNumberOfRuns() // Number of runs
    val totalDistance: Flow<Float> = runDao.getTotalDistance() // Total distance
    val maxRunID: Flow<Int> = runDao.getMaxRunID() // Maximum run ID
    val totalDuration: Flow<Int> = runDao.getTotalDuration() // Total duration
    val numberOfPins: Flow<Int> = runDao.getNumberOfPins() // Number of pins
    val longestDistance: Flow<Float> = runDao.getLongestDistance() // Longest distance
    val longestDuration: Flow<Int> = runDao.getLongestDuration() // Longest duration
    val maxBurnedCalories: Flow<Float> = runDao.getMaxCalories() // Maximum burned calories
    val totalCaloriesBurned: Flow<Float> = runDao.getTotalCalories() // Total burned calories

    @WorkerThread
    fun getByDate(dateTime: LocalDateTime): Flow<List<Run>> {
        return runDao.getByDate(dateTime) // Get runs by date
    }

    @WorkerThread
    fun getByID(ID: Int): Flow<Run> {
        return runDao.getByID(ID) // Get run by ID
    }

    @WorkerThread
    fun insertRun(run: Run) {
        runDao.insert(run) // Insert a run
    }

    @WorkerThread
    fun insertRuns(vararg runs: Run) {
        runDao.insertAll(*runs) // Insert multiple runs
    }

    @WorkerThread
    fun deleteByID(runID: Int) {
        runDao.deleteByID(runID) // Delete run by ID
        runDao.deletePinByID(runID) // Delete pin by run ID
    }

    @WorkerThread
    fun delete(run: Run) {
        runDao.delete(run) // Delete a run
    }

    @WorkerThread
    fun deleteAll() {
        runDao.deleteAll() // Delete all runs
    }

    @WorkerThread
    fun updateRun(run: Run) {
        runDao.update(run) // Update a run
    }

    @WorkerThread
    fun insertPin(pin: Pin) {
        runDao.insertPin(pin) // Insert a pin
    }

    @WorkerThread
    fun getPins(runID: Int): Flow<List<Pin>> {
        return runDao.getPins(runID) // Get pins by run ID
    }

    @WorkerThread
    fun getAllPins(): Flow<List<Pin>> {
        return runDao.getAllPins() // Get all pins
    }
}
