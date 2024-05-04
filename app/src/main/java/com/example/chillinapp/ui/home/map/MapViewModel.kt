package com.example.chillinapp.ui.home.map

import androidx.lifecycle.ViewModel
import com.example.chillinapp.data.stress.StressDataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel(private val dataService : StressDataService) : ViewModel() {

    // Mutable state flow for the UI state of the map screen
    private val _uiState = MutableStateFlow(MapUiState())

    // State flow for the UI state of the map screen
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    /*TODO: Add functions to update the UI state of the map screen*/

}