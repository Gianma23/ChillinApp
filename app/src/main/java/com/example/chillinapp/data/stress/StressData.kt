package com.example.chillinapp.data.stress

data class StressRawData(
    val timestamp: Long = 0,
    val edaSensor: Float = 0.0f,
    val skinTemperatureSensor: Float = 0.0f,
    val heartrateSensor: Float = 0.0f,
    val latitude: Double,
    val longitude: Double
)

data class StressDerivedData (
    val timestamp: Long = 0,
    val BINTERVAL: Array<Float> = arrayOf(0.0f, 0.0f),
    val prediction: Double = 0.0,
    val stressLevel: Float = 0.0f
)