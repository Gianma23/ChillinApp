package com.example.chillinapp.ui.home.monitor

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FormattedStressRawData(
    timestamp: Long,
    heartRateSensor: Float,
    skinTemperatureSensor: Float,
    edaSensor:Float
) {
    val timestamp: String
    val heartRateSensor: Float
    val skinTemperatureSensor: Float
    val edaSensor:Float

    init {
        this.timestamp = timestampToString(timestamp)
        this.heartRateSensor = heartRateSensor.toFloat()
        this.skinTemperatureSensor = skinTemperatureSensor.toFloat()
        this.edaSensor=edaSensor.toFloat()
    }
}

fun timestampToString(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun timestampToHourOfDay(timestamp: String): Float {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS", Locale.getDefault())
    val date = sdf.parse(timestamp)
    val calendar = Calendar.getInstance().apply {
        if (date != null) {
            time = date
        }
    }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    return hour + minute / 60.0f + second / 3600.0f
}