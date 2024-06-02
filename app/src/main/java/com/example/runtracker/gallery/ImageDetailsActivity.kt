package com.example.runtracker.gallery

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.runtracker.R
import com.squareup.picasso.Picasso
import java.io.File
import kotlin.math.abs

/**
 * Activity for displaying details of images associated with a specific run.
 */
class ImageDetailsActivity : AppCompatActivity() {

    private var latitude: Double = 0.0 // Latitude of the location
    private var longitude: Double = 0.0 // Longitude of the location
    private var runID: Int = 0 // ID of the run
    private var filesPath = ArrayList<String>() // List of image file paths
    private var filesSize = 0 // Total number of image files
    private var currentPosition = 0 // Current position in the image list
    private lateinit var imageView: ImageView // ImageView to display images
    private lateinit var leftButton: ImageButton // Button to navigate to the previous image
    private lateinit var rightButton: ImageButton // Button to navigate to the next image
    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences for app settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_details)

        // Initialize UI elements
        imageView = findViewById(R.id.image)
        leftButton = findViewById(R.id.leftButton)
        rightButton = findViewById(R.id.rightButton)
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Get data from intent
        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        runID = intent.getIntExtra("runID", 0)

        // Set the appearance of the app based on settings
        setAppAppearance()

        // Load photos associated with the run
        getPhotos()

        // Set up button visibility and click listeners
        if (filesSize > 1) {
            rightButton.visibility = View.VISIBLE
        }
        rightButton.setOnClickListener {
            currentPosition++
            uploadPhoto()
        }
        leftButton.setOnClickListener {
            currentPosition--
            uploadPhoto()
        }

        // Display the first photo
        uploadPhoto()
    }

    /**
     * Upload and display the current photo.
     */
    private fun uploadPhoto() {
        val cw = ContextWrapper(this)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val subDirectory = File(directory, "$runID")
        val myImageFile = File(subDirectory, filesPath[currentPosition])
        setButtonsVisibility()

        Picasso.get().load(myImageFile).into(imageView)
    }

    /**
     * Set visibility of navigation buttons based on the current position.
     */
    private fun setButtonsVisibility() {
        leftButton.visibility =
            if (currentPosition > 0) View.VISIBLE else View.INVISIBLE
        rightButton.visibility =
            if (currentPosition < filesSize - 1) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Load photos from the directory associated with the run.
     */
    private fun getPhotos() {
        val cw = ContextWrapper(this)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val subDirectory = File(directory, "$runID")
        val files = subDirectory.listFiles { file ->
            file.absolutePath.contains((abs(latitude) + abs(longitude)).toString().replace(".", ""))
        }

        if (files != null) {
            filesSize = files.size
            for (file in files) {
                filesPath.add(file.name)
            }
        }
    }

    /**
     * Set the appearance of the app based on user settings.
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
}
