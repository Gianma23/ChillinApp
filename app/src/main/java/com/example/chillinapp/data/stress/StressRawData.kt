package com.example.chillinapp.data.stress

data class StressRawData(
    val timestamp: Long = 0,
    val edaSensor: Float = 0.0f,
    val skinTemperatureSensor: Float = 0.0f,
    val heartRateSensor: Float = 0.0f,
    val latitude: Double? = null,
    val longitude: Double? = null
)

