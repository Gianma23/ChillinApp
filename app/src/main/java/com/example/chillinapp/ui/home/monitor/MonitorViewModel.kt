package com.example.chillinapp.ui.home.monitor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.data.stress.StressRawData
import com.example.chillinapp.simulation.generateStressRawDataList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class MonitorViewModel(
//    private val dataService :  StressDataService
): ViewModel(){

    // Mutable state flow for the UI state of the overall screen
    private val _uiState = MutableStateFlow(MonitorUiState())

    // State flow for the UI state of the overall screen
    val uiState: StateFlow<MonitorUiState> = _uiState.asStateFlow()

    companion object{
        // Number of items to display initially (one each 30 seconds)
        const val STEP_SIZE: Long = 1000 * 60
    }

    init {


        // Set the initial UI state to a loading state
        _uiState.value = MonitorUiState(
            stressData = emptyList(),
            fieldValuesMap = mapOf(
                "heartRateSensor" to emptyList(),
                "skinTemperatureSensor" to emptyList()
            ),
            error = null,
            isLoading = true
        )
        Log.d("MonitorViewModel", "Initial data loading started.")

        retrieveInitialData()
    }

    private fun retrieveInitialData() {
        viewModelScope.launch(Dispatchers.IO) {

    //  TODO: Get the initial data from the data service and update the UI state with it.
    //            val startingData: ServiceResult<List<StressRawData>, StressErrorType> =
    //                dataService.getRawData(n = DISPLAY_LENGTH)

            // Simulate the starting data
            val startingData: ServiceResult<List<StressRawData>, StressErrorType> =
                retrieveDailySimulatedSata()

            // Simulate a network error
            // val startingData: ServiceResult<List<StressRawData>, StressErrorType> =
            //    networkErrorSimulation()

            if (startingData.success.not()) {
                Log.e("MonitorViewModel", "Error loading initial data: ${startingData.error}")
                _uiState.update {
                    it.copy(
                        error = startingData.error,
                        isLoading = false
                    )
                }
                return@launch
            }
            Log.d(
                "MonitorViewModel",
                "Initial data loaded successfully. (${startingData.data?.size} items)"
            )

            // Order by timestamp
            startingData.data?.sortedBy { it.timestamp }

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

            // Create a map where the key is the field name and the value is a list of pairs (timestamp, field value)
            temporalMapping()

            // Set the loading state to false
            _uiState.update {
                it.copy(isLoading = false)
            }

        }
    }

    private fun retrieveDailySimulatedSata(): ServiceResult<List<StressRawData>, StressErrorType> {
        return ServiceResult(
            success = true,
            data = generateStressRawDataList(
                start = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                },
                end = Calendar.getInstance(),
                step = STEP_SIZE,
                invalidDataProbability = 0.2
            ),
            error = null
        )
    }

//    private suspend fun networkErrorSimulation(): ServiceResult<List<StressRawData>, StressErrorType> {
//        delay(4000)
//        val startingData: ServiceResult<List<StressRawData>, StressErrorType> = ServiceResult(
//            success = false,
//            data = null,
//            error = StressErrorType.NETWORK_ERROR
//        )
//        return startingData
//    }

    private fun temporalMapping() {
        _uiState.value.stressData.forEach { data ->
            FormattedStressRawData::class.java.declaredFields
                .filter { it.name != "timestamp" && it.name != "\$stable" }
                .forEach { field ->
                    field.isAccessible = true
                    field.get(data)?.let { value ->
                        _uiState.update {
                            it.copy(
                                fieldValuesMap = it.fieldValuesMap + mapOf(
                                    field.name to (it.fieldValuesMap[field.name]
                                        ?: emptyList()) + Pair(data.timestamp, value)
                                )
                            )
                        }
                    }
                }
        }
    }
}

