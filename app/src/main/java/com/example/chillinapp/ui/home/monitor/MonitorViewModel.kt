package com.example.chillinapp.ui.home.monitor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MonitorViewModel: ViewModel(){

    // Mutable state flow for the UI state of the overall screen
    private val _uiState = MutableStateFlow(MonitorUiState())

    // State flow for the UI state of the overall screen
    val uiState: StateFlow<MonitorUiState> = _uiState.asStateFlow()

    /*TODO: Add functions to update the UI state of the overall screen*/

}