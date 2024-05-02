package com.example.chillinapp.ui.home.monitor

import com.example.chillinapp.data.stress.StressErrorType

data class MonitorUiState (

    val stressData: List<FormattedStressRawData> = emptyList(),
    val error: StressErrorType? = null
)