package com.example.runtracker.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runtracker.R
import com.example.runtracker.settingsItem

/**
 * Activity for managing app settings.
 */
class SettingsActivity : AppCompatActivity(), SettingsAdapter.OnSettingsItemClickListener {
    private lateinit var recyclerViewSettings: RecyclerView // RecyclerView for displaying settings items
    private var settingsItems = ArrayList<settingsItem>() // List of settings items
    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences for storing settings
    private lateinit var editor: SharedPreferences.Editor // SharedPreferences editor

    /**
     * Called when the activity is starting.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val title = findViewById<TextView>(R.id.text)
        title.text = "Settings"

        addToSettingsItems()
        initRecyclerView()
        setAppAppearance()
    }

    /**
     * Initializes the RecyclerView for displaying settings items.
     */
    private fun initRecyclerView() {
        recyclerViewSettings = findViewById(R.id.recyclerViewSettings)
        recyclerViewSettings.layoutManager = GridLayoutManager(applicationContext, 1)
        recyclerViewSettings.adapter = SettingsAdapter(settingsItems, this)
    }

    /**
     * Adds items to the settings list.
     */
    private fun addToSettingsItems() {
        val itemAppColor = settingsItem("Set app color", R.drawable.color_picker, false)
        val itemDarkMode = settingsItem("Dark mode", R.drawable.dark_mode, true)
        settingsItems.add(itemAppColor)
        settingsItems.add(itemDarkMode)
    }

    /**
     * Sets the dark mode appearance if enabled in settings.
     */
    private fun setDarkMode() {
        if (sharedPreferences.getBoolean("darkMode", false)) {
            val header = findViewById<LinearLayout>(R.id.linearLayout)
            val background = findViewById<ConstraintLayout>(R.id.main)
            header.setBackgroundColor(Color.BLACK)
            background.setBackgroundColor(Color.rgb(170, 170, 170))
        }
    }

    /**
     * Sets the overall appearance of the app based on user settings.
     */
    private fun setAppAppearance() {
        val header = findViewById<LinearLayout>(R.id.linearLayout)
        val background = findViewById<ConstraintLayout>(R.id.main)
        if (sharedPreferences.getBoolean("darkMode", false)) {
            header.setBackgroundColor(Color.BLACK)
            background.setBackgroundColor(Color.rgb(170, 170, 170))
        } else {
            background.setBackgroundColor(Color.rgb(255, 255, 255))
            header.setBackgroundColor(sharedPreferences.getInt("color", Color.BLACK))
        }
    }

    /**
     * Handles click events for settings items.
     * @param position The position of the clicked item.
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onBlockClick(position: Int) {
        if (position == 0) {
            val colorIntent = Intent(this, ColorPickerActivity::class.java)
            resultLauncher.launch(colorIntent)
        }
        setAppAppearance()
        recyclerViewSettings.adapter?.notifyDataSetChanged()
    }

    /**
     * Handles the result from the ColorPickerActivity.
     */
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            if (data != null) {
                data.getIntExtra("color", Color.BLACK).let {
                    editor.putInt("color", it)
                    editor.apply()
                }
                val header = findViewById<LinearLayout>(R.id.linearLayout)
                header.setBackgroundColor(sharedPreferences.getInt("color", Color.BLACK))
            }
        }

    /**
     * Called when the activity is resumed.
     */
    override fun onResume() {
        super.onResume()
        val header = findViewById<LinearLayout>(R.id.linearLayout)
        header.setBackgroundColor(sharedPreferences.getInt("color", Color.BLACK))
        setDarkMode()
    }
}
