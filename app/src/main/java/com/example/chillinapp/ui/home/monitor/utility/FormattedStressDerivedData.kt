package com.example.chillinapp.ui.home.monitor.utility

/**
 * Data class representing the formatted derived stress data.
 *
 * @property millis The timestamp in milliseconds.
 * @property stressLevel The calculated stress level.
 * @property lowerBound The lower bound of the stress level.
 * @property upperBound The upper bound of the stress level.
 * @property timestamp The timestamp in string format, default value is the string representation of [millis].
 * @property dummy A boolean flag indicating whether the data is dummy or not, default value is false.
 */
data class FormattedStressDerivedData(
    val millis: Long,
    val stressLevel: Float,
    val lowerBound: Float,
    val upperBound: Float,
    val timestamp: String = timestampToString(millis),
    val dummy: Boolean = false
)
