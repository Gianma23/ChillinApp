package com.example.chillinapp.ui.home.monitor

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormattedStressRawData(
    timestamp: Long,
    heartRateSensor: Double,
    skinTemperatureSensor: Double
) {
    val timestamp: String
    val heartRateSensor: Double
    val skinTemperatureSensor: Double

    init {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS", Locale.getDefault())
        this.timestamp = sdf.format(Date(timestamp))
        this.heartRateSensor = heartRateSensor
        this.skinTemperatureSensor = skinTemperatureSensor
    }
}