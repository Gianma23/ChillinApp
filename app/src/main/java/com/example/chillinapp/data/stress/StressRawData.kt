package com.example.chillinapp.data.stress

/**
 * Data class representing raw stress data.
 *
 * This class holds raw stress data, including a timestamp, readings from various sensors, and optional latitude and longitude coordinates.
 * The sensor readings include data from an EDA sensor, a skin temperature sensor, and a heart rate sensor.
 *
 * @property timestamp The timestamp of the data, represented as a long. Defaults to 0.
 * @property edaSensor A float representing the reading from the EDA sensor. Defaults to 0.0f.
 * @property skinTemperatureSensor A float representing the reading from the skin temperature sensor. Defaults to 0.0f.
 * @property heartRateSensor A float representing the reading from the heart rate sensor. Defaults to 0.0f.
 * @property latitude An optional double representing the latitude coordinate where the data was collected. Defaults to null.
 * @property longitude An optional double representing the longitude coordinate where the data was collected. Defaults to null.
 */
data class StressRawData(
    val timestamp: Long = 0,
    val edaSensor: Float = 0.0f,
    val skinTemperatureSensor: Float = 0.0f,
    val heartRateSensor: Float = 0.0f,
    val latitude: Double? = null,
    val longitude: Double? = null
)

