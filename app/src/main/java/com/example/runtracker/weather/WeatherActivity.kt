package com.example.runtracker.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.runtracker.R
import com.example.weatherapp.model.Weather.CurrentWeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity for displaying weather information.
 */
class WeatherActivity : AppCompatActivity() {

    private val API_KEY = "9c894a0b83495c75cce93fd0d89fabb3" // OpenWeatherMap API key
    private lateinit var fusedLocationClient: FusedLocationProviderClient // Client for accessing location services

    private lateinit var cityName: TextView
    private lateinit var temperature: TextView
    private lateinit var weatherCondition: TextView
    private lateinit var minmaxTemp: TextView
    private lateinit var sunriseTime: TextView
    private lateinit var sunsetTime: TextView
    private lateinit var windSpeed: TextView
    private lateinit var pressure: TextView
    private lateinit var humidity: TextView

    /**
     * Called when the activity is starting.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        cityName = findViewById(R.id.city_text_view)
        temperature = findViewById(R.id.temp_text_view)
        weatherCondition = findViewById(R.id.weather_text_view)
        minmaxTemp = findViewById(R.id.min_max_text_view)
        sunriseTime = findViewById(R.id.sunrise_text_view)
        sunsetTime = findViewById(R.id.sunset_text_view)
        windSpeed = findViewById(R.id.wind_text_view)
        pressure = findViewById(R.id.pressure_text_view)
        humidity = findViewById(R.id.humidity_text_view)

        checkLocationPermissionAndFetchWeather()
    }

    /**
     * Fetches weather data based on latitude and longitude.
     * @param lat Latitude
     * @param lon Longitude
     */
    private fun fetchWeatherData(lat: Double, lon: Double) {
        val weatherClient = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)

        val call = weatherClient.getWeather(lat, lon, "metric", API_KEY)
        call.enqueue(object : Callback<CurrentWeatherResponse> {
            override fun onResponse(call: Call<CurrentWeatherResponse>, response: Response<CurrentWeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { weatherResponse ->
                        updateWeatherData(weatherResponse)
                    } ?: run {
                        Toast.makeText(applicationContext, "No weather data available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Something went wrong: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Error fetching weather: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Updates the UI with the weather data.
     * @param weather Weather data
     */
    private fun updateWeatherData(weather: CurrentWeatherResponse) {
        cityName.text = weather.name
        temperature.text = "${weather.main.temp}°C"
        weatherCondition.text = weather.weather[0].main
        minmaxTemp.text = "Min Temp: ${weather.main.temp_min}°C, Max Temp: ${weather.main.temp_max}°C"
        sunriseTime.text = convertTimestampToDate(weather.sys.sunrise, weather.timezone)
        sunsetTime.text = convertTimestampToDate(weather.sys.sunset, weather.timezone)
        windSpeed.text = weather.wind.speed.toString()
        pressure.text = weather.main.pressure.toString()
        humidity.text = weather.main.humidity.toString()
    }

    /**
     * Converts a timestamp to a formatted date string.
     * @param timestamp Unix timestamp
     * @param timezone Timezone offset in seconds
     * @return Formatted date string
     */
    private fun convertTimestampToDate(timestamp: Long, timezone: Int): String {
        val date = Date((timestamp + timezone) * 1000)
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(date)
    }

    /**
     * Checks location permission and fetches weather data if granted.
     */
    private fun checkLocationPermissionAndFetchWeather() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getLastKnownLocation()
        }
    }

    /**
     * Gets the last known location and fetches weather data.
     */
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                fetchWeatherData(it.latitude, it.longitude)
            } ?: run {
                Toast.makeText(applicationContext, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Handles the result of the location permission request.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastKnownLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    /**
     * Retrofit API service for fetching weather data.
     */
    interface WeatherApiService {
        @GET("weather")
        fun getWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("units") units: String = "metric",
            @Query("appid") apiKey: String
        ): Call<CurrentWeatherResponse>
    }
}
