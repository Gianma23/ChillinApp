package com.example.chillinapp.ui.home.monitor

import com.example.chillinapp.data.stress.StressErrorType

data class MonitorUiState (
    val fieldValuesMap: Map<String, List<Pair<String, Any>>> = emptyMap(),
    val stressData: List<FormattedStressRawData> = emptyList(),
    val error: StressErrorType? = null,
    val isLoading: Boolean = true
)