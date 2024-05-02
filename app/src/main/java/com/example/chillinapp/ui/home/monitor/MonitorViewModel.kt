package com.example.chillinapp.ui.home.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.data.stress.StressRawData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MonitorViewModel(
//    private val dataService :  StressDataService
): ViewModel(){

    // Mutable state flow for the UI state of the overall screen
    private val _uiState = MutableStateFlow(MonitorUiState())

    // State flow for the UI state of the overall screen
    val uiState: StateFlow<MonitorUiState> = _uiState.asStateFlow()

    companion object {
        private const val DISPLAY_LENGTH = 30
    }

    init {

        viewModelScope.launch{

            /*TODO: Get the initial data from the data service and update the UI state with it.*/
//            val startingData: ServiceResult<List<StressRawData>, StressErrorType> =
//                dataService.getRawData(n = DISPLAY_LENGTH)

            // Simulate the starting data
            val startingData: ServiceResult<List<StressRawData>, StressErrorType> =
                ServiceResult(
                    success = true,
                    data = generateStressRawDataList(),
                    error = null
                )

            // Update the UI state with the starting data
            _uiState.value = MonitorUiState(
                stressData = startingData.data?.map { rawData ->
                    FormattedStressRawData(
                        timestamp = rawData.timestamp,
                        heartRateSensor = rawData.heartRateSensor,
                        skinTemperatureSensor = rawData.skinTemperatureSensor
                    )
                } ?: emptyList(),
                error = startingData.error
            )
        }
    }

    private fun generateStressRawDataList(): List<StressRawData> {
        val list = mutableListOf<StressRawData>()
        val currentTimeMillis = System.currentTimeMillis()

        for (hour in 0 until 24) {
            for (minute in 0 until 60) {
                val timestamp = currentTimeMillis - TimeUnit.HOURS.toMillis((23 - hour).toLong()) - TimeUnit.MINUTES.toMillis((59 - minute).toLong())
                val stressRawData = StressRawData(
                    timestamp = timestamp,
                    heartRateSensor = (60..100).random().toDouble(), // Random value between 60 and 100
                    skinTemperatureSensor = (30..40).random().toDouble() // Random value between 30 and 40
                )
                list.add(stressRawData)
            }
        }
        return list
    }

}