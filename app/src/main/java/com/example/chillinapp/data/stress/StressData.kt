package com.example.chillinapp.data.stress

data class StressRawData (
    val timestamp: Long = 0,
    val edaSensor: Double = 0.0,
    val skinTemperatureSensor: Double = 0.0
)

data class StressDerivedData (
    val timestamp: Long = 0,
    val BINTERVAL: Array<Float> = arrayOf(0.0f, 0.0f),
    val prediction: Double = 0.0,
    val stressLevel: Float = 0.0f
)