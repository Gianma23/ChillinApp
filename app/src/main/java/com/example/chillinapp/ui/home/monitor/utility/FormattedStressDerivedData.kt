package com.example.chillinapp.ui.home.monitor.utility

data class FormattedStressDerivedData(
    val millis: Long,
    val stressLevel: Float,
    val lowerBound: Float,
    val upperBound: Float,
    val timestamp: String = timestampToString(millis),
)
