package com.example.chillinapp.ui.home.overall

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OverallViewModel: ViewModel(){

    // Mutable state flow for the UI state of the overall screen
    private val _uiState = MutableStateFlow(OverallUiState())

    // State flow for the UI state of the overall screen
    val uiState: StateFlow<OverallUiState> = _uiState.asStateFlow()

    /*TODO: Add functions to update the UI state of the overall screen*/

}