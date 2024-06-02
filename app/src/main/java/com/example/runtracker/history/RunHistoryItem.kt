package com.example.runtracker.history

import java.time.DayOfWeek
import java.time.LocalDateTime

/**
 * Data class representing a run history item.
 * @param runID The ID of the run.
 * @param dateTime The date and time of the run.
 * @param distance The distance of the run in kilometers.
 * @param duration The duration of the run in seconds.
 */
class RunHistoryItem(
    private var runID: Int,
    private var dateTime: LocalDateTime,
    var distance: Float,
    var duration: Int
) {
    /**
     * Gets the name of the run, including the day of the week and the time of day.
     * @return A string representing the name of the run.
     */
    fun getName(): String {
        return "${getDayOfWeek(dateTime.dayOfWeek)} ${getTimeOfDay(dateTime.hour)} Run"
    }

    /**
     * Gets the time of day based on the hour.
     * @param hour The hour of the day (0-23).
     * @return A string representing the time of day.
     */
    private fun getTimeOfDay(hour: Int): String {
        return when {
            hour < 5 || hour > 21 -> "Night"
            hour < 12 -> "Morning"
            hour < 19 -> "Afternoon"
            else -> "Evening"
        }
    }

    /**
     * Gets the day of the week as a string.
     * @param day The DayOfWeek enum value.
     * @return A string representing the day of the week.
     */
    private fun getDayOfWeek(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.SUNDAY -> "Sunday"
            DayOfWeek.MONDAY -> "Monday"
            DayOfWeek.TUESDAY -> "Tuesday"
            DayOfWeek.WEDNESDAY -> "Wednesday"
            DayOfWeek.THURSDAY -> "Thursday"
            DayOfWeek.FRIDAY -> "Friday"
            else -> "Saturday"
        }
    }

    /**
     * Gets the ID of the run.
     * @return The ID of the run.
     */
    fun getRunID(): Int {
        return runID
    }
}
