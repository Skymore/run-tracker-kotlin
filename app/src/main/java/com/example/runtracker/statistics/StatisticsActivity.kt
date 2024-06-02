package com.example.runtracker.statistics

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.runtracker.R
import com.example.runtracker.RunApplication
import com.example.runtracker.database.RunModelFactory
import com.example.runtracker.database.RunViewModel

/**
 * Activity for displaying run statistics.
 */
class StatisticsActivity : AppCompatActivity() {
    private val viewModel: RunViewModel by viewModels {
        RunModelFactory((application as RunApplication).repository)
    }

    /**
     * Called when the activity is starting.
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Observe the total number of runs
        viewModel.numberOfRuns.observe(this) {
            if (it != null) {
                findViewById<TextView>(R.id.textViewTotalRuns).text =
                    "Total runs:\n$it"
            }
        }

        // Observe the total distance run
        viewModel.totalDistance.observe(this) {
            if (it != null) {
                findViewById<TextView>(R.id.textViewTotalDistance).text =
                    "Total distance:\n${StringFormatter.getInstance().formatDistance(it)}"
            }
        }

        // Observe the total duration of runs
        viewModel.totalDuration.observe(this) {
            if (it != null) {
                findViewById<TextView>(R.id.textViewTotalDuration).text =
                    "Total duration:\n${StringFormatter.getInstance().formatTime(it)}"
            }
        }

        // Observe the total number of photos taken
        viewModel.numberOfPins.observe(this) {
            if (it != null) {
                findViewById<TextView>(R.id.textViewTotalPhotos).text =
                    "Total photos:\n$it"
            }
        }

        // Observe the longest distance run
        viewModel.longestDistance.observe(this) {
            if (it != null) {
                findViewById<TextView>(R.id.textViewLongestDistance).text =
                    "Longest distance:\n${StringFormatter.getInstance().formatDistance(it)}"
            }
        }

        // Observe the longest duration of a run
        viewModel.longestDuration.observe(this) {
            if (it != null) {
                findViewById<TextView>(R.id.textViewLongestDuration).text =
                    "Longest duration:\n${StringFormatter.getInstance().formatTime(it)}"
            }
        }

        // Observe the total calories burned
        viewModel.totalBurnedCalories.observe(this) {
            if (it != null) {
                val calories = String.format("%.1f", it)
                findViewById<TextView>(R.id.textViewTotalCalories).text =
                    "Total calories burnt: $calories kcal"
            }
        }

        // Observe the maximum calories burned in a single run
        viewModel.maxBurnedCalories.observe(this) {
            if (it != null) {
                val calories = String.format("%.1f", it)
                findViewById<TextView>(R.id.textViewMaxCalories).text =
                    "Most calories burnt: $calories kcal"
                findViewById<TextView>(R.id.textViewTotalCalories).text =
                    "Total burned calories:\n$it kcal"
            }
        }
    }
}
