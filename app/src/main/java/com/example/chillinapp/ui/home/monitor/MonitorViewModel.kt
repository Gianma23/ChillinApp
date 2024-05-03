package com.example.chillinapp.ui.home.monitor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.data.stress.StressRawData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
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

            Log.d("MonitorViewModel", "Initial data loaded:")
            for (data in _uiState.value.stressData) {
                Log.d("MonitorViewModel", "Timestamp: ${data.timestamp}, Heart Rate: ${data.heartRateSensor}, Skin Temperature: ${data.skinTemperatureSensor}")
            }
        }
    }

    private fun generateStressRawDataList(): List<StressRawData> {
        val list = mutableListOf<StressRawData>()
        val now = Calendar.getInstance()
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val hours = TimeUnit.MILLISECONDS.toHours(now.timeInMillis - startOfDay.timeInMillis).toInt()
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.timeInMillis - startOfDay.timeInMillis).toInt() % 60

        for (hour in 0..hours) {
            val endMinute = if (hour == hours) minutes else 59
            for (minute in 0..endMinute) {
                val timestamp = startOfDay.timeInMillis + TimeUnit.HOURS.toMillis(hour.toLong()) + TimeUnit.MINUTES.toMillis(minute.toLong())
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