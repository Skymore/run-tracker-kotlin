package com.example.runtracker.database

import androidx.lifecycle.*
import java.time.LocalDateTime

/**
 * ViewModel for managing Run and Pin data.
 */
class RunViewModel(private val repository: RunRepository) : ViewModel() {
    var runs: LiveData<List<Run>> = repository.allRuns.asLiveData() // All runs
    var numberOfRuns: LiveData<Int> = repository.numberOfRuns.asLiveData() // Number of runs
    var totalDistance: LiveData<Float> = repository.totalDistance.asLiveData() // Total distance
    var maxRunID: LiveData<Int> = repository.maxRunID.asLiveData() // Maximum run ID
    var totalDuration: LiveData<Int> = repository.totalDuration.asLiveData() // Total duration
    var numberOfPins: LiveData<Int> = repository.numberOfPins.asLiveData() // Number of pins
    var longestDistance: LiveData<Float> = repository.longestDistance.asLiveData() // Longest distance
    var longestDuration: LiveData<Int> = repository.longestDuration.asLiveData() // Longest duration
    var maxBurnedCalories: LiveData<Float> = repository.maxBurnedCalories.asLiveData() // Maximum burned calories
    var totalBurnedCalories: LiveData<Float> = repository.totalCaloriesBurned.asLiveData() // Total burned calories

    fun runsByDate(dateTime: LocalDateTime): LiveData<List<Run>> {
        return repository.getByDate(dateTime).asLiveData() // Runs by date
    }

    fun runByID(ID: Int): LiveData<Run> {
        return repository.getByID(ID).asLiveData() // Run by ID
    }

    fun insertRun(run: Run) {
        repository.insertRun(run) // Insert a run
    }

    fun insertRuns(vararg runs: Run) {
        repository.insertRuns(*runs) // Insert multiple runs
    }

    fun deleteByID(runID: Int) {
        repository.deleteByID(runID) // Delete run by ID
    }

    fun deleteRun(run: Run) {
        repository.delete(run) // Delete a run
    }

    fun deleteAllRuns() {
        repository.deleteAll() // Delete all runs
    }

    fun updateRun(run: Run) {
        repository.updateRun(run) // Update a run
    }

    fun insertPin(pin: Pin) {
        repository.insertPin(pin) // Insert a pin
    }

    fun getPins(runID: Int): LiveData<List<Pin>> {
        return repository.getPins(runID).asLiveData() // Get pins by run ID
    }

    fun getAllPins(): LiveData<List<Pin>> {
        return repository.getAllPins().asLiveData() // Get all pins
    }
}

/**
 * Factory for creating RunViewModel instances.
 */
class RunModelFactory(private val repository: RunRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunViewModel::class.java)) {
            return RunViewModel(repository) as T // Create RunViewModel
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
