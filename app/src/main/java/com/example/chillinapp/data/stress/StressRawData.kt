package com.example.chillinapp.data.stress

data class StressRawData(
    val timestamp: Long = 0,
    val heartRateSensor: Float = 0.0F,
    val skinTemperatureSensor: Float = 0.0F,
    val edaSensor: Float= 0.0F
)

