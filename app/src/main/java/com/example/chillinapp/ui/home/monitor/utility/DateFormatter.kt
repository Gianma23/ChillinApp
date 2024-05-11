package com.example.chillinapp.ui.home.monitor.utility

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Converts a timestamp to a string representation.
 *
 * @param timestamp The timestamp to be converted.
 * @return A string representation of the timestamp in the format "dd/MM/yyyy HH:mm:ss:SSS".
 */
fun timestampToString(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Converts a timestamp to the hour of the day.
 *
 * @param timestamp The timestamp to be converted.
 * @return A float representing the hour of the day, including minutes and seconds as fractions of an hour.
 */
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

/**
 * Converts a timestamp to milliseconds.
 *
 * @param timestamp The timestamp to be converted.
 * @return The number of milliseconds represented by the timestamp.
 */
fun timestampToMillis(timestamp: String): Long {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS", Locale.getDefault())
    val date = sdf.parse(timestamp)
    return date?.time ?: 0
}

/**
 * Converts a timestamp to a time string.
 *
 * @param timestamp The timestamp to be converted.
 * @return A string representation of the time in the format "HH:mm:ss".
 */
fun timestampToTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}