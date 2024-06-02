package com.example.weatherapp.model.Weather

import com.google.gson.annotations.SerializedName


//TODO
// Create data class CurrentWeatherResponse (Refer to API Response)
// Hint: Refer to Wind Data Class
data class CurrentWeatherResponse(
    val name: String, // City name
    val main: Main,
    val sys: Sys,
    val weather: List<WeatherItem>,
    val wind: Wind,

    @SerializedName("dt")
    val dt: Long,  // Update time

    @SerializedName("timezone")
    val timezone: Int = 0,
)