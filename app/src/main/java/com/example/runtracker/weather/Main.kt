package com.example.weatherapp.model.Weather

import com.google.gson.annotations.SerializedName

//TODO
// Create data class Main (Refer to API Response)
// Hint: Refer to Wind Data Class
data class Main(
    @SerializedName("temp")
    val temp: Float,

    @SerializedName("temp_min")
    val temp_min: Float,

    @SerializedName("temp_max")
    val temp_max: Float,

    @SerializedName("pressure")
    val pressure: Int,

    @SerializedName("humidity")
    val humidity: Int
)