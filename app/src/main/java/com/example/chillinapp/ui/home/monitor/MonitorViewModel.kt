package com.example.chillinapp.ui.home.monitor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.stress.StressDataService
import com.example.chillinapp.data.stress.StressDerivedData
import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.data.stress.StressRawData
import com.example.chillinapp.ui.home.monitor.utility.FormattedStressDerivedData
import com.example.chillinapp.ui.home.monitor.utility.FormattedStressRawData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class MonitorViewModel(
    private val dataService : StressDataService
): ViewModel(){

    // Mutable state flow for the UI state of the overall screen
    private val _uiState = MutableStateFlow(MonitorUiState())

    // State flow for the UI state of the overall screen
    val uiState: StateFlow<MonitorUiState> = _uiState.asStateFlow()

    init {
        Log.d("MonitorViewModel", "Initializing MonitorViewModel...")
        // Set the initial UI state to a loading state
        _uiState.value = MonitorUiState(
            physiologicalData = emptyList(),
            physiologicalMappedData = mapOf(
                "heartRateSensor" to emptyList(),
                "skinTemperatureSensor" to emptyList(),
                "edaSensor" to emptyList()
            ),
            physiologicalError = null,
            isPhysiologicalDataLoading = true
        )

        retrieveData()
    }

    private fun retrieveData() {

        // Set the loading state to true
        _uiState.update {
            it.copy(
                isPhysiologicalDataLoading = true,
                isStressDataLoading = true
            )
        }

        // Retrieve the data
        retrievePhysiologicalData()
//        retrieveStressData()
    }

    private fun retrieveStressData() {
        viewModelScope.launch(Dispatchers.IO) {

            Log.d("MonitorViewModel", "Loading stress data...")
            // Retrieve the data
            val response: ServiceResult<List<StressDerivedData>, StressErrorType> =
                dataService.getDerivedData(
                    startTime = Calendar.getInstance().apply {
                        time = _uiState.value.day
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis,
                    endTime = Calendar.getInstance().apply {
                        time = _uiState.value.day
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }.timeInMillis
                )

            if (response.success.not()) {
                Log.e("MonitorViewModel", "Error loading initial data: ${response.error}")
                _uiState.update {
                    it.copy(
                        stressError = response.error,
                        isStressDataLoading = false
                    )
                }
                return@launch
            }
            Log.d(
                "MonitorViewModel",
                "Initial data loaded successfully. (${response.data?.size} items)"
            )

            // Order by timestamp
            response.data?.sortedBy { it.timestamp }

            // Update the UI state with the starting data
            _uiState.update {
                it.copy(
                    stressData = response.data?.map { derivedData ->
                        FormattedStressDerivedData(
                            millis = derivedData.timestamp,
                            stressLevel = derivedData.stressLevel,
                            lowerBound = derivedData.bInterval.minOrNull()?: 0f,
                            upperBound = derivedData.bInterval.maxOrNull()?: 0f
                        )
                    } ?: emptyList(),
                    stressError = response.error
                )
            }

            // Set the loading state to false
            _uiState.update {
                it.copy(isStressDataLoading = false)
            }
            Log.d("MonitorViewModel", "Stress data loading finished.")
        }
    }

    private fun retrievePhysiologicalData() {
        viewModelScope.launch(Dispatchers.IO) {

            Log.d("MonitorViewModel", "Loading physiological data...")
            // Retrieve the data
            val response: ServiceResult<List<StressRawData>, StressErrorType> =
                dataService.getRawData(
                    startTime = Calendar.getInstance().apply {
                        time = _uiState.value.day
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis,
                    endTime = Calendar.getInstance().apply {
                        time = _uiState.value.day
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }.timeInMillis
                )

            // Simulate the starting data
//            val response: ServiceResult<List<StressRawData>, StressErrorType> =
//                retrieveDailySimulatedSata()

            // Simulate a network physiologicalError
            // val startingData: ServiceResult<List<StressRawData>, StressErrorType> =
            //    networkErrorSimulation()

            if (response.success.not()) {
                Log.e("MonitorViewModel", "Error loading initial data: ${response.error}")
                _uiState.update {
                    it.copy(
                        physiologicalError = response.error,
                        isPhysiologicalDataLoading = false
                    )
                }
                return@launch
            }
            Log.d(
                "MonitorViewModel",
                "Initial data loaded successfully. (${response.data?.size} items)"
            )

            // Order by timestamp
            response.data?.sortedBy { it.timestamp }

            // Update the UI state with the starting data
            _uiState.value = MonitorUiState(
                physiologicalData = response.data?.map { rawData ->
                    FormattedStressRawData(
                        millis = rawData.timestamp,
                        skinTemperatureSensor = rawData.skinTemperatureSensor,
                        edaSensor = rawData.edaSensor
                    )
                } ?: emptyList(),
                physiologicalError = response.error
            )

            // Create a map where the key is the field name and the value is a list of pairs (timestamp, field value)
            temporalMapping()

            // Set the loading state to false
            _uiState.update {
                it.copy(isPhysiologicalDataLoading = false)
            }
            Log.d("MonitorViewModel", "Physiological data loading finished.")
        }
    }

//    private fun retrieveDailySimulatedSata(): ServiceResult<List<StressRawData>, StressErrorType> {
//        return ServiceResult(
//            success = true,
//            data = generateStressRawDataList(
//                start = Calendar.getInstance().apply {
//                    set(Calendar.HOUR_OF_DAY, 0)
//                    set(Calendar.MINUTE, 0)
//                    set(Calendar.SECOND, 0)
//                    set(Calendar.MILLISECOND, 0)
//                },
//                end = Calendar.getInstance(),
//                step = STEP_SIZE,
//                invalidDataProbability = 0.2
//            ),
//            error = null
//        )
//    }

//    private suspend fun networkErrorSimulation(): ServiceResult<List<StressRawData>, StressErrorType> {
//        delay(4000)
//        val startingData: ServiceResult<List<StressRawData>, StressErrorType> = ServiceResult(
//            success = false,
//            data = null,
//            physiologicalError = StressErrorType.NETWORK_ERROR
//        )
//        return startingData
//    }

    private fun temporalMapping() {
        _uiState.value.physiologicalData.forEach { data ->
            FormattedStressRawData::class.java.declaredFields
                .filter { it.name != "timestamp" && it.name != "\$stable" }
                .forEach { field ->
                    field.isAccessible = true
                    field.get(data)?.let { value ->
                        _uiState.update {
                            it.copy(
                                physiologicalMappedData = it.physiologicalMappedData + mapOf(
                                    field.name to ((it.physiologicalMappedData[field.name])?.plus(
                                        Pair(data.timestamp, value)) ?: listOf(Pair(data.timestamp, value))
                                    )
                                )
                            )
                        }
                    }
                }
        }
        Log.d("MonitorViewModel", "Temporal mapping finished: ${_uiState.value.physiologicalMappedData}")
    }

    fun previousDay() {
        _uiState.update {
            it.copy(day = Calendar.getInstance().apply {
                time = it.day
                add(Calendar.DAY_OF_MONTH, -1)
            }.time)
        }
        retrieveData()
    }

    fun nextDay() {
        _uiState.update {
            it.copy(day = Calendar.getInstance().apply {
                time = it.day
                add(Calendar.DAY_OF_MONTH, 1)
            }.time)
        }
        retrieveData()
    }

    fun isToday(): Boolean {
        return Calendar.getInstance().apply {
            time = _uiState.value.day
        }.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    }

    fun formatDate(): String {
        return when {
            isToday() -> "Today"
            else -> {
                val calendar = Calendar.getInstance().apply {
                    time = _uiState.value.day
                }
                "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
            }
        }

    }
}

