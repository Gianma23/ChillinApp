package com.example.chillinapp.ui.home.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    // Mutable state flow for the UI state of the settings screen
    private val _uiState = MutableStateFlow(SettingsUiState())

    // State flow for the UI state of the settings screen
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /*TODO: Add functions to update the UI state of the settings screen*/

}