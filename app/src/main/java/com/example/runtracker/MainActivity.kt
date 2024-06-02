package com.example.runtracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runtracker.gallery.GalleryActivity
import com.example.runtracker.history.HistoryActivity
import com.example.runtracker.menu.MenuAdapter
import com.example.runtracker.menu.MenuItem
import com.example.runtracker.myAccount.MyAccountActivity
import com.example.runtracker.runRecording.MapActivity
import com.example.runtracker.settings.SettingsActivity
import com.example.runtracker.statistics.StatisticsActivity
import com.example.runtracker.weather.WeatherActivity

/**
 * Main activity of the application which displays a menu for navigation.
 */
class MainActivity : AppCompatActivity(), MenuAdapter.OnBlockClickListener {
    private lateinit var recyclerViewMenuItems: RecyclerView // RecyclerView to display menu items
    private var menuItems: ArrayList<MenuItem> = ArrayList() // List to store menu items
    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences for app settings
    private lateinit var editor: SharedPreferences.Editor // Editor for SharedPreferences

    /**
     * Called when the activity is starting.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Disable night mode
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE) // Initialize SharedPreferences
        editor = sharedPreferences.edit() // Initialize SharedPreferences editor
        val title = findViewById<TextView>(R.id.text) // Find the title TextView
        title.text = "Main menu" // Set the title text
        addMenuItems() // Add menu items to the list
        setView() // Set up the RecyclerView
    }

    /**
     * Set up the RecyclerView.
     */
    private fun setView() {
        recyclerViewMenuItems = findViewById(R.id.recyclerViewMenuItems) // Find the RecyclerView
        recyclerViewMenuItems.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) // Set layout manager
        recyclerViewMenuItems.adapter = MenuAdapter(menuItems, this) // Set adapter
    }

    /**
     * Add menu items to the list.
     */
    private fun addMenuItems() {
        addToMenuItemsList("START ACTIVITY", R.drawable.activites)
        addToMenuItemsList("WEATHER", R.drawable.weathericon)
        addToMenuItemsList("GALLERY", R.drawable.camera)
        addToMenuItemsList("STATISTICS", R.drawable.statistics)
        addToMenuItemsList("MY ACCOUNT", R.drawable.account)
        addToMenuItemsList("HISTORY", R.drawable.history)
        addToMenuItemsList("SETTINGS", R.drawable.settings)
    }

    /**
     * Helper function to add a menu item to the list.
     * @param data The title of the menu item.
     * @param drawable The icon resource of the menu item.
     */
    private fun addToMenuItemsList(data: String, drawable: Int) {
        menuItems.add(MenuItem(data, drawable))
    }

    /**
     * Handle menu item click events.
     * @param position The position of the clicked item.
     * @param date The date associated with the item (not used).
     */
    override fun onBlockClick(position: Int, date: String) {
        when (position) {
            0 -> {
                Intent(this, MapActivity::class.java).also {
                    if (isLocationPermissionGranted()) {
                        Log.i("mymap", "Localization permission granted")
                        startActivity(it)
                    }
                }
            }
            1 -> {
                Intent(this, WeatherActivity::class.java).also {
                    startActivity(it)
                }
            }
            2 -> {
                Intent(this, GalleryActivity::class.java).also {
                    startActivity(it)
                }
            }
            3 -> {
                Intent(this, StatisticsActivity::class.java).also {
                    startActivity(it)
                }
            }
            4 -> {
                Intent(this, MyAccountActivity::class.java).also {
                    startActivity(it)
                }
            }
            5 -> {
                Intent(this, HistoryActivity::class.java).also {
                    startActivity(it)
                }
            }
            6 -> {
                Intent(this, SettingsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    /**
     * Called when the activity is resumed.
     */
    @SuppressLint("ResourceAsColor")
    override fun onResume() {
        super.onResume()
        val background = findViewById<ConstraintLayout>(R.id.constraint) // Find the main background layout
        val header = findViewById<LinearLayout>(R.id.linearLayout) // Find the header layout
        if (sharedPreferences.getBoolean("darkMode", false)) {
            // If dark mode is enabled, set dark colors
            val newColor = Color.rgb(170, 170, 170)
            background.setBackgroundColor(newColor)
            header.setBackgroundColor(Color.BLACK)
        } else {
            // If dark mode is disabled, set light colors
            header.setBackgroundColor(sharedPreferences.getInt("color", Color.BLACK))
            background.setBackgroundColor(Color.WHITE)
        }
    }

    /**
     * Check if location permission is granted.
     * @return True if permission is granted, false otherwise.
     */
    private fun isLocationPermissionGranted(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            return false
        } else {
            return true
        }
    }
}
