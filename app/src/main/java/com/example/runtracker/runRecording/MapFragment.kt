package com.example.runtracker.runRecording

import android.annotation.SuppressLint
import android.content.*
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runtracker.BuildConfig
import com.example.runtracker.R
import com.example.runtracker.RunApplication
import com.example.runtracker.database.Pin
import com.example.runtracker.database.Run
import com.example.runtracker.database.RunModelFactory
import com.example.runtracker.database.RunViewModel
import com.example.runtracker.gallery.CameraActivity
import com.example.runtracker.gallery.ImageDetailsActivity
import com.example.runtracker.statistics.StringFormatter
import com.github.clans.fab.FloatingActionButton
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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.time.LocalDateTime

/**
 * Fragment for displaying and recording a running activity on a map.
 */
class MapFragment : Fragment(), SensorEventListener {
    companion object {
        const val ACTIVITY_STARTED = "activityStarted"
        const val ACTIVITY_PAUSED = "activityPaused"
        const val ACTIVITY_STOPPED = "activityStopped"
    }

    private val runViewModel: RunViewModel by viewModels {
        RunModelFactory((requireActivity().application as RunApplication).repository)
    }

    private var activityStatus: String = ""

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var addPhotoButton: FloatingActionButton
    private lateinit var startButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var mapView: MapView
    private lateinit var timeTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var paceTextView: TextView
    private lateinit var stepsTextView: TextView
    private lateinit var caloriesTextView: TextView

    private lateinit var serviceIntent: Intent
    private var time = 0.0

    private lateinit var trackerIntent: Intent
    private var distance: Float = 0f
    private var pace: Float = 0f
    private var calories: Float = 0f

    private lateinit var mapController: IMapController
    private lateinit var myGpsMyLocationProvider: GpsMyLocationProvider
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var currentLocation: GeoPoint
    private var points: MutableList<GeoPoint> = mutableListOf()
    private val pins: ArrayList<OverlayItem> = ArrayList()
    var currentRunID: Int = 0

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepsSinceReboot: Float = 0f
    private var initialStepCount: Float = -1f

    /**
     * Called to initialize the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("my_account", Context.MODE_PRIVATE)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize text views
        timeTextView = view.findViewById(R.id.timeValueTextView) as TextView
        distanceTextView = view.findViewById(R.id.distanceTextView) as TextView
        paceTextView = view.findViewById(R.id.paceTextView) as TextView
        stepsTextView = view.findViewById(R.id.stepsTextView) as TextView
        caloriesTextView = view.findViewById(R.id.caloriesTextView) as TextView

        // Initialize buttons
        addPhotoButton = view.findViewById(R.id.addPhotoFAB) as FloatingActionButton
        startButton = view.findViewById(R.id.startButton) as ImageButton
        stopButton = view.findViewById(R.id.stopButton) as ImageButton

        startButton.setOnClickListener {
            if (activityStatus != ACTIVITY_STARTED) {
                startActivity()
                startButton.setImageResource(R.drawable.pause_button_image)
            } else {
                pauseActivity()
                startButton.setImageResource(R.drawable.play_button_image)
            }
        }

        stopButton.setOnClickListener {
            if (activityStatus != ACTIVITY_STOPPED) {
                stopActivity()
                startButton.setImageResource(R.drawable.play_button_image)
            }
        }

        // Initialize map
        mapView = view.findViewById(R.id.mapView) as MapView

        val context = requireActivity().applicationContext
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        mapView.setUseDataConnection(true)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        mapController = mapView.controller
        mapController.zoomTo(18, 1)

        val defaultLocation = GeoPoint(47.655548, -122.303200) // University of Washington
        mapController.animateTo(defaultLocation)

        // Set current location
        myGpsMyLocationProvider = GpsMyLocationProvider(activity)
        myLocationOverlay = MyLocationNewOverlay(myGpsMyLocationProvider, mapView)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        myLocationOverlay.isDrawAccuracyEnabled = true

        val icon = BitmapFactory.decodeResource(
            resources,
            org.osmdroid.library.R.drawable.ic_menu_compass
        )
        myLocationOverlay.setPersonIcon(icon)
        mapView.overlays.add(myLocationOverlay)

        myLocationOverlay.runOnFirstFix {
            val myLocation: GeoPoint = myLocationOverlay.myLocation
            requireActivity().runOnUiThread {
                mapView.controller.animateTo(myLocation)
            }
        }

        addPhotoButton.setOnClickListener {
            if (activityStatus == ACTIVITY_STARTED) {
                addPhoto()
            }
        }

        // Initialize sensor manager and step sensor
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(requireContext(), "No step sensor detected on this device", Toast.LENGTH_SHORT).show()
        }

        // Initialize timer service
        serviceIntent = Intent(requireContext(), TimerService::class.java)
        requireActivity().registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        // Initialize tracker service
        trackerIntent = Intent(requireContext(), TrackerService::class.java)
        requireActivity().registerReceiver(updateTrack, IntentFilter(TrackerService.TRACKER_UPDATED))

        // Observe max run ID from ViewModel
        runViewModel.maxRunID.observe(viewLifecycleOwner) { maxRunID ->
            currentRunID = (maxRunID ?: 0) + 1
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount == -1f) {
                initialStepCount = event.values[0]
            }

            stepsSinceReboot = event.values[0] - initialStepCount
            stepsTextView.text = "Steps: ${stepsSinceReboot.toInt()}"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    /**
     * Creates a pin at the current location on the map.
     */
    private fun createPin() {
        val currentPinLocation = LocationHelper.getLastKnownLocation(myLocationOverlay)
        val point = GeoPoint(currentPinLocation.latitude, currentPinLocation.longitude)
        val overlayItem = OverlayItem("Pin", "", point)
        pins.add(overlayItem)
        val overlay = ItemizedIconOverlay(
            pins,
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    val intent = Intent(requireContext(), ImageDetailsActivity::class.java)
                    intent.putExtra("latitude", item.point.latitude)
                    intent.putExtra("longitude", item.point.longitude)
                    if (activityStatus == ACTIVITY_STOPPED) {
                        intent.putExtra("runID", currentRunID - 1)
                    } else {
                        intent.putExtra("runID", currentRunID)
                    }
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

    /**
     * Adds a GeoPoint to the database.
     * @param geoPoint The GeoPoint to add.
     * @param path The path of the image associated with the GeoPoint.
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun addGeoPointToDataBase(geoPoint: GeoPoint, path: String) {
        val geoPointData = Pin(0, geoPoint, path, currentRunID)
        GlobalScope.launch {
            runViewModel.insertPin(geoPointData)
        }
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            timeTextView.text = StringFormatter.getInstance().formatTime(time)

            // Update pace
            if (time != 0.0 && distance > 0) {
                val timeInMinutes = time / 60.0
                val distanceInKm = distance / 1000f
                pace = timeInMinutes.toFloat() / distanceInKm // paceMinPerKm
                paceTextView.text = StringFormatter.getInstance().formatPace(pace)
            }
        }
    }

    private val updateTrack: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("UseCompatLoadingForDrawables")
        override fun onReceive(context: Context, intent: Intent) {
            // Read current location
            val latitude = intent.getDoubleExtra(TrackerService.LAT_EXTRA, 0.0)
            val longitude = intent.getDoubleExtra(TrackerService.LON_EXTRA, 0.0)
            val location = GeoPoint(latitude, longitude)
            points.add(location)

            // Update distance
            if (activityStatus == ACTIVITY_STARTED) {
                distance = intent.getFloatExtra(TrackerService.DIST_EXTRA, 0f) // Distance in meters
                // Calculate calories
                val weight = sharedPreferences.getString("Weight", "0")?.toFloat() ?: 0f
                calories = weight * distance / 1000
            }

            if (distance != 0f) {
                distanceTextView.text = StringFormatter.getInstance().formatDistance(distance / 1000f)
                caloriesTextView.text = "Calories: ${calories.toInt()} kcal"
            }

            // Draw track
            val pointsArrayList = ArrayList<GeoPoint>(points)
            val polylineTrack = Polyline()
            polylineTrack.setPoints(pointsArrayList)
            mapView.overlays.add(polylineTrack)
            mapView.invalidate()
        }
    }

    /**
     * Starts the running activity.
     */
    private fun startActivity() {
        Toast.makeText(requireContext(), "Running started!", Toast.LENGTH_SHORT).show()

        // Change status
        activityStatus = ACTIVITY_STARTED

        // Get current location
        currentLocation = LocationHelper.getLastKnownLocation(myLocationOverlay)
        points.add(currentLocation)

        startTimer()
        startTracker()
    }

    private fun startTracker() {
        trackerIntent.putExtra(TrackerService.LAT_EXTRA, currentLocation.latitude)
        trackerIntent.putExtra(TrackerService.LON_EXTRA, currentLocation.longitude)
        trackerIntent.putExtra(TrackerService.DIST_EXTRA, distance)

        requireActivity().startService(trackerIntent)
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        requireActivity().startService(serviceIntent)
    }

    /**
     * Pauses the running activity.
     */
    private fun pauseActivity() {
        Toast.makeText(requireContext(), "Running paused!", Toast.LENGTH_SHORT).show()

        // Change status
        activityStatus = ACTIVITY_PAUSED

        pauseTimer()
        pauseTracker()
    }

    private fun pauseTracker() {
        requireActivity().stopService(trackerIntent)
    }

    private fun pauseTimer() {
        requireActivity().stopService(serviceIntent)
    }

    /**
     * Stops the running activity and saves the run data.
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun stopActivity() {
        Toast.makeText(requireContext(), "Running stopped!", Toast.LENGTH_SHORT).show()

        // Change status
        activityStatus = ACTIVITY_STOPPED

        // Save recorded run in database
        val weight = sharedPreferences.getString("Weight", "0")?.takeIf { it.isNotBlank() } ?: "0"
        val calories = weight.toFloat() * distance / 1000
        val dateTime = LocalDateTime.now()
        val run = Run(0, dateTime, distance / 1000, time.toInt(), points, calories)
        GlobalScope.launch {
            runViewModel.insertRun(run)
        }

        resetTimer()
        stopTracker()
    }

    private fun stopTracker() {
        requireActivity().stopService(trackerIntent)

        // Clear live data variables
        distance = 0f
        calories = 0f
        distanceTextView.text = "0.000 km"
        paceTextView.text = "00:00 min/km"
        caloriesTextView.text = "Calories: 0 kcal"
    }

    private fun resetTimer() {
        pauseTimer()

        // Clear time
        time = 0.0
        timeTextView.text = StringFormatter.getInstance().formatTime(time)
    }

    /**
     * Adds a photo at the current location.
     */
    private fun addPhoto() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        val currentPinLocation = LocationHelper.getLastKnownLocation(myLocationOverlay)
        intent.putExtra("latitude", currentPinLocation.latitude)
        intent.putExtra("longitude", currentPinLocation.longitude)
        intent.putExtra("runID", currentRunID)
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        if (data != null) {
            val path = data.getStringExtra("image_path")
            val latitude = data.getDoubleExtra("latitude", 0.0)
            val longitude = data.getDoubleExtra("longitude", 0.0)
            val geoPoint = GeoPoint(latitude, longitude)
            if (path != null) {
                addGeoPointToDataBase(geoPoint, path)
                createPin()
            }
        }
    }
}
