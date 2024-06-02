package com.example.runtracker.history

import android.content.Intent
import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.runtracker.BuildConfig
import com.example.runtracker.R
import com.example.runtracker.RunApplication
import com.example.runtracker.database.RunModelFactory
import com.example.runtracker.database.RunViewModel
import com.example.runtracker.gallery.ImageDetailsActivity
import com.example.runtracker.runRecording.MapFragment
import com.example.runtracker.statistics.StringFormatter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import java.io.File

/**
 * Activity for displaying a summary of a run, including details and a map.
 */
class SummaryActivity : AppCompatActivity() {
    private var runID: Int = -1 // ID of the run

    // ViewModel for managing Run data
    private val viewModel: RunViewModel by viewModels {
        RunModelFactory((application as RunApplication).repository)
    }

    private lateinit var textViewDistance: TextView // TextView for displaying run distance
    private lateinit var textViewTime: TextView // TextView for displaying run duration
    private lateinit var textViewPace: TextView // TextView for displaying run pace
    private lateinit var textCalories: TextView // TextView for displaying calories burnt
    private lateinit var mapView: MapView // MapView for displaying run route
    private lateinit var mapController: IMapController // Map controller for MapView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        // Initialize UI elements
        textViewDistance = findViewById(R.id.textViewDistance)
        textViewTime = findViewById(R.id.textViewTime)
        textViewPace = findViewById(R.id.textViewPace)
        mapView = findViewById(R.id.mapView)
        textCalories = findViewById(R.id.textCalories)

        // Map setup
        val context = applicationContext
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        mapView.setUseDataConnection(true)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        mapController = mapView.controller
        mapController.zoomTo(18, 1)

        // Read run data
        if (intent != null) {
            runID = intent.getIntExtra("runID", -1)
            viewModel.runByID(runID).observe(this) { run ->
                if (run == null) return@observe

                // Calculate time, distance, pace, and calories
                val timeSeconds = run.duration
                val distanceKilometers = run.distance
                val timeMinutes: Float = timeSeconds / 60f
                val paceMinPerKm: Float = timeMinutes / distanceKilometers
                val calories = String.format("%.2f", run.calories)

                // Set text views
                textViewDistance.text = StringFormatter.getInstance().formatDistance(distanceKilometers)
                textViewTime.text = StringFormatter.getInstance().formatTime(timeSeconds)
                textViewPace.text = StringFormatter.getInstance().formatPace(paceMinPerKm)
                textCalories.text = "Calories burnt: $calories kcal"

                // Draw track on map
                val pointsArrayList = ArrayList<GeoPoint>(run.points)
                val polylineTrack = Polyline()
                polylineTrack.setPoints(pointsArrayList)
                mapView.overlays.add(polylineTrack)
                mapView.invalidate()

                // Set map's starting location
                val defaultLocation = run.points[0]
                mapController.animateTo(defaultLocation)
            }

            // Load pins and add them to the map
            val pins: ArrayList<OverlayItem> = ArrayList()
            viewModel.getPins(runID).observe(this) { pinList ->
                for (pin in pinList) {
                    val overlayItem = OverlayItem("Pin", "", pin.geoPoint)
                    pins.add(overlayItem)
                    val overlay = ItemizedIconOverlay(
                        pins,
                        object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                            override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                                val intent = Intent(context, ImageDetailsActivity::class.java)
                                intent.putExtra("latitude", item.point.latitude)
                                intent.putExtra("longitude", item.point.longitude)
                                intent.putExtra("runID", runID)

                                startActivity(intent)
                                return true
                            }

                            override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                                return false
                            }
                        },
                        context
                    )
                    mapView.overlays.add(overlay)
                }
                mapView.invalidate()
            }
        }

        // Set delete button click listener
        findViewById<Button>(R.id.buttonDeleteRun).setOnClickListener {
            buttonDeleteClicked(it)
        }
    }

    /**
     * Deletes the run and associated photos.
     * @param view The view that was clicked.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun buttonDeleteClicked(view: View) {
        GlobalScope.launch {
            deletePhotos(runID)
            viewModel.deleteByID(runID)
            finish()
        }
    }

    /**
     * Deletes photos associated with the run.
     * @param runID The ID of the run.
     */
    private fun deletePhotos(runID: Int) {
        val cw = ContextWrapper(this)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val subDirectory = File(directory, "$runID")

        subDirectory.deleteRecursively()
    }
}
