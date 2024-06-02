package com.example.runtracker.myAccount

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runtracker.R

/**
 * Activity for displaying and managing user account information.
 */
class MyAccountActivity : AppCompatActivity(), MyAccountAdapter.OnMyAccountClickListener {

    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences for storing account information
    private lateinit var sharedPreferencesSettings: SharedPreferences // SharedPreferences for storing settings
    private lateinit var editor: SharedPreferences.Editor // Editor for modifying shared preferences
    private lateinit var recyclerViewAccount: RecyclerView // RecyclerView for displaying account items
    private var myAccountItems = ArrayList<String>() // List of account items

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("my_account", Context.MODE_PRIVATE)
        sharedPreferencesSettings = getSharedPreferences("settings", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Set the title of the activity
        val title = findViewById<TextView>(R.id.text)
        title.text = "My Account"

        // Add account items to the list
        addMenuItems()

        // Set up the RecyclerView
        setView()

        // Set the appearance of the app based on settings
        setAppAppearance()
    }

    /**
     * Sets up the RecyclerView with a GridLayoutManager and adapter.
     */
    private fun setView() {
        recyclerViewAccount = findViewById(R.id.recyclerViewAccount)
        recyclerViewAccount.layoutManager = GridLayoutManager(applicationContext, 1)
        recyclerViewAccount.adapter = MyAccountAdapter(myAccountItems, this)
    }

    /**
     * Sets the appearance of the app based on user settings.
     */
    private fun setAppAppearance() {
        val header = findViewById<LinearLayout>(R.id.linearLayout)
        val background = findViewById<ConstraintLayout>(R.id.main)
        if (sharedPreferencesSettings.getBoolean("darkMode", false)) {
            header.setBackgroundColor(Color.BLACK)
            background.setBackgroundColor(Color.rgb(170, 170, 170))
        } else {
            background.setBackgroundColor(Color.rgb(255, 255, 255))
            header.setBackgroundColor(sharedPreferencesSettings.getInt("color", Color.BLACK))
        }
    }

    /**
     * Adds predefined items to the account items list.
     */
    private fun addMenuItems() {
        addToAccountItemsList("Name")
        addToAccountItemsList("Surname")
        addToAccountItemsList("Sex")
        addToAccountItemsList("Date of birth")
        addToAccountItemsList("Weight")
        addToAccountItemsList("Height")
        addToAccountItemsList("BMI")
    }

    /**
     * Adds an item to the account items list.
     * @param data The data to be added to the list.
     */
    private fun addToAccountItemsList(data: String) {
        myAccountItems.add(data)
    }

    /**
     * Handles click events on account items.
     * @param position The position of the clicked item.
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onBlockClick(position: Int) {
        recyclerViewAccount.adapter?.notifyDataSetChanged()
    }
}
