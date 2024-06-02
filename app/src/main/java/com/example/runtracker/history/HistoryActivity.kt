package com.example.runtracker.history

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runtracker.*
import com.example.runtracker.database.Run
import com.example.runtracker.database.RunModelFactory
import com.example.runtracker.database.RunViewModel

/**
 * Activity for displaying the history of runs.
 */
class HistoryActivity : AppCompatActivity(), RunHistoryAdapter.OnRunItemClickListener {

    private lateinit var recyclerViewHistory: RecyclerView // RecyclerView to display the list of runs
    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences for app settings

    // ViewModel for managing Run data
    private val runViewModel: RunViewModel by viewModels {
        RunModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Initialize UI elements
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory)
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Observe LiveData from ViewModel and update UI accordingly
        runViewModel.runs.observe(this) {
            updateRecyclerView(it)
        }

        // Set the appearance of the app based on settings
        setAppAppearance()

        // Set the title of the activity
        val title = findViewById<TextView>(R.id.text)
        title.text = "History"
    }

    /**
     * Converts a list of Run objects to a list of RunHistoryItem objects.
     * @param runs List of Run objects.
     * @return ArrayList of RunHistoryItem objects.
     */
    private fun getRunHistoryItems(runs: List<Run>): ArrayList<RunHistoryItem> {
        val runHistoryItems = ArrayList<RunHistoryItem>()
        runs.forEach {
            runHistoryItems.add(RunHistoryItem(it.id, it.dateTime, it.distance, it.duration))
        }
        return runHistoryItems
    }

    /**
     * Updates the RecyclerView with a new list of runs.
     * @param runs List of Run objects.
     */
    private fun updateRecyclerView(runs: List<Run>) {
        recyclerViewHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewHistory.adapter = RunHistoryAdapter(getRunHistoryItems(runs), this)
    }

    /**
     * Handles click events on run items.
     * @param position Position of the clicked item.
     * @param runID ID of the clicked run.
     */
    override fun onClick(position: Int, runID: Int) {
        Intent(this, SummaryActivity::class.java).apply {
            putExtra("runID", runID)
        }.also {
            startActivity(it)
        }
    }

    /**
     * Sets the appearance of the app based on user settings.
     */
    private fun setAppAppearance() {
        val header = findViewById<LinearLayout>(R.id.linearLayout)
        val background = findViewById<ConstraintLayout>(R.id.main)
        if (sharedPreferences.getBoolean("darkMode", false)) {
            // Set dark mode appearance
            header.setBackgroundColor(Color.BLACK)
            background.setBackgroundColor(Color.rgb(170, 170, 170))
        } else {
            // Set light mode appearance
            background.setBackgroundColor(Color.rgb(255, 255, 255))
            header.setBackgroundColor(sharedPreferences.getInt("color", Color.BLACK))
        }
    }
}
