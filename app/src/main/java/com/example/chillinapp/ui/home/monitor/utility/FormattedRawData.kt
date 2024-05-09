package com.example.chillinapp.ui.home.monitor.utility

data class FormattedStressRawData(
    val millis: Long,
    val skinTemperatureSensor: Float,
    val edaSensor: Float,
    val timestamp: String = timestampToString(millis),
)
