package com.example.runtracker.myAccount

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.runtracker.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

/**
 * ViewHolder for displaying and managing user account information in a RecyclerView.
 * @param view The view of the item.
 * @param onBlockClickListener Listener for block click events.
 */
class MyAccountViewHolder(
    var view: View,
    private var onBlockClickListener: MyAccountAdapter.OnMyAccountClickListener
) : RecyclerView.ViewHolder(view), View.OnClickListener {

    private var sharedPreferences: SharedPreferences = view.context.getSharedPreferences("my_account", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()
    private val spinnerData = listOf("F", "M") // Data for the gender spinner
    private var spinner: Spinner // Spinner for selecting gender
    private val inputText = EditText(view.context) // EditText for entering text

    var textViewTitle: TextView = view.findViewById(R.id.textViewTitle) // TextView for displaying the title
    var textViewData: TextView = view.findViewById(R.id.textViewData) // TextView for displaying the data

    private val spinnerAdapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, spinnerData)

    init {
        itemView.setOnClickListener(this)
        inputText.gravity = Gravity.CENTER

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner = Spinner(view.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            adapter = spinnerAdapter
        }
    }

    /**
     * Handles click events on the item view.
     * @param v The view that was clicked.
     */
    override fun onClick(v: View?) {
        when (adapterPosition) {
            0 -> createInputAlert("Name", "Enter your name").show()
            1 -> createInputAlert("Surname", "Enter your surname").show()
            2 -> createGenderAlert().show()
            3 -> createDateAlert().show()
            4 -> createInputAlert("Weight", "Enter your weight").show()
            5 -> createInputAlert("Height", "Enter your height").show()
        }
    }

    /**
     * Creates an alert dialog for entering text input.
     * @param input The type of input (e.g., Name, Surname).
     * @param title The title of the dialog.
     * @return The created AlertDialog.
     */
    private fun createInputAlert(input: String, title: String): AlertDialog {
        val parent = inputText.parent as? ViewGroup
        parent?.removeView(inputText)

        return AlertDialog.Builder(view.context)
            .setTitle(title)
            .setView(inputText)
            .setPositiveButton("OK") { dialog, _ ->
                val text = inputText.text.toString()
                if (input == "Name" || input == "Surname") {
                    editor.putString(input, text)
                    editor.apply()
                    onBlockClickListener.onBlockClick(adapterPosition)
                } else if (text.toDoubleOrNull() != null) {
                    editor.putString(input, text)
                    editor.apply()
                    calculateBMI()
                } else {
                    Toast.makeText(view.context, "Enter valid data!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    /**
     * Creates an alert dialog for selecting gender.
     * @return The created AlertDialog.
     */
    private fun createGenderAlert(): AlertDialog {
        val spinner = Spinner(view.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, spinnerData)
        }

        return AlertDialog.Builder(view.context)
            .setTitle("Select option")
            .setView(spinner)
            .setPositiveButton("OK") { dialog, _ ->
                val selectedOption = spinner.selectedItem as String
                editor.putString("Sex", selectedOption)
                editor.apply()
                onBlockClickListener.onBlockClick(adapterPosition)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    /**
     * Creates an alert dialog for selecting date of birth.
     * @return The created AlertDialog.
     */
    private fun createDateAlert(): AlertDialog {
        val calendarView = CalendarView(view.context)
        return AlertDialog.Builder(view.context)
            .setView(calendarView)
            .setPositiveButton("OK") { dialog, _ ->
                val selectedDate = Date(calendarView.date)
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate)
                editor.putString("Date of birth", formattedDate)
                editor.apply()
                onBlockClickListener.onBlockClick(adapterPosition)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    /**
     * Calculates the BMI based on the stored height and weight, and updates the BMI value.
     */
    fun calculateBMI() {
        val heightString = sharedPreferences.getString("Height", "")
        val weightString = sharedPreferences.getString("Weight", "")
        if (!heightString.isNullOrEmpty() && !weightString.isNullOrEmpty()) {
            val height = heightString.toFloat() / 100.0
            val weight = weightString.toFloat()
            val bmi = weight / height.pow(2.0)
            editor.putString("BMI", String.format("%.2f", bmi))
            editor.apply()
            onBlockClickListener.onBlockClick(6)
        }
    }
}
