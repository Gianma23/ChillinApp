package com.example.chillinapp.ui.home.monitor.utils.classes

import com.example.chillinapp.ui.home.monitor.utils.timestampToString

/**
 * Data class representing the formatted raw stress data.
 *
 * @property millis The timestamp in milliseconds.
 * @property heartRateSensor The heart rate sensor data.
 * @property skinTemperatureSensor The skin temperature sensor data.
 * @property edaSensor The electrodermal activity sensor data.
 * @property timestamp The timestamp in string format, default value is the string representation of [millis].
 * @property dummy A boolean flag indicating whether the data is dummy or not, default value is false.
 */
data class FormattedStressRawData(
    val millis: Long,
    val heartRateSensor: Float,
    val skinTemperatureSensor: Float,
    val edaSensor: Float,
    val timestamp: String = timestampToString(millis),
    val dummy: Boolean = false
)