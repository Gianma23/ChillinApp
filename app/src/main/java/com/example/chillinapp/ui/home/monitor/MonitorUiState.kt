package com.example.chillinapp.ui.home.monitor

import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.ui.home.monitor.utils.FormattedStressDerivedData
import com.example.chillinapp.ui.home.monitor.utils.FormattedStressRawData
import java.util.Date

/**
 * Data class representing the UI state of the monitor screen.
 *
 * @property physiologicalMappedData A map of sensor names to their corresponding list of data pairs. Each pair consists of a string representing the timestamp and an Any object representing the sensor value.
 * @property physiologicalData A list of formatted raw stress data.
 * @property physiologicalError An error type indicating any error that occurred while fetching the physiological data.
 * @property isPhysiologicalDataLoading A boolean flag indicating whether the physiological data is currently being loaded.
 * @property stressData A list of formatted derived stress data.
 * @property stressError An error type indicating any error that occurred while fetching the stress data.
 * @property isStressDataLoading A boolean flag indicating whether the stress data is currently being loaded.
 * @property day The current date.
 */
data class MonitorUiState(

    val physiologicalMappedData: Map<String, List<Pair<String, Any>>> = emptyMap(),
    val physiologicalData: List<FormattedStressRawData> = emptyList(),
    val physiologicalError: StressErrorType? = null,
    val isPhysiologicalDataLoading: Boolean = true,

    val stressData: List<FormattedStressDerivedData> = emptyList(),
    val stressError: StressErrorType? = null,
    val isStressDataLoading: Boolean = true,

    val day: Date = Date()
)