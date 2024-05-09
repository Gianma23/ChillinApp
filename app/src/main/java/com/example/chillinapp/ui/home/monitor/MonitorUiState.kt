package com.example.chillinapp.ui.home.monitor

import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.ui.home.monitor.utility.FormattedStressDerivedData
import com.example.chillinapp.ui.home.monitor.utility.FormattedStressRawData
import java.util.Date

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