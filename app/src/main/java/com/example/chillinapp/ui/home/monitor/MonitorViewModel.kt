package com.example.chillinapp.ui.home.monitor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.stress.StressDataService
import com.example.chillinapp.data.stress.StressDerivedData
import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.data.stress.StressRawData
import com.example.chillinapp.ui.home.monitor.utils.classes.FormattedStressDerivedData
import com.example.chillinapp.ui.home.monitor.utils.classes.FormattedStressRawData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.pow

/**
 * ViewModel for the Monitor screen. It handles the business logic for fetching and processing stress data.
 *
 * @property dataService The service used to fetch stress data.
 */
class MonitorViewModel(
    private val dataService : StressDataService
): ViewModel(){

    // Mutable state flow for the UI state of the overall screen
    private val _uiState = MutableStateFlow(MonitorUiState())

    // State flow for the UI state of the overall screen
    val uiState: StateFlow<MonitorUiState> = _uiState.asStateFlow()

    companion object{
        const val STRESS_THRESHOLD = 0.6F
        const val STRESS_STEP_SIZE = 1000L * 60 * 5 // 5 minutes
        const val PHYSIO_STEP_SIZE = 1000L * 60 * 5 // 5 minute
        const val STRESS_IMPORTANCE = 6.0
    }

    /**
     * Initializes the MonitorViewModel by setting the initial UI state to a loading state and fetching the data.
     */
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

    /**
     * Function to retrieve stress and physiological data.
     */
    private fun retrieveData() {

        val startTime = Calendar.getInstance().apply {
            time = _uiState.value.day
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val endTime = Calendar.getInstance().apply {
            time = _uiState.value.day
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        Log.d("MonitorViewModel", "Loading data for day ${_uiState.value.day}...")
        Log.d("MonitorViewModel", "Start time: $startTime")
        Log.d("MonitorViewModel", "End time: $endTime")

        // Set the loading state to true
        _uiState.update {
            it.copy(
                isPhysiologicalDataLoading = true,
                isStressDataLoading = true
            )
        }

        // Retrieve the data
        viewModelScope.launch(Dispatchers.IO) {
            retrieveStressData(startTime, endTime)
            retrievePhysiologicalData(startTime, endTime)
        }
    }

    /**
     * Function to retrieve stress data.
     *
     * @param startTime The start time for the data retrieval.
     * @param endTime The end time for the data retrieval.
     */
    private suspend fun retrieveStressData(startTime: Long, endTime: Long) {
        Log.d("MonitorViewModel", "Loading stress data...")
        // Retrieve the data
        val response: ServiceResult<List<StressDerivedData>, StressErrorType> =
            dataService.getDerivedData(
                startTime = startTime,
                endTime = endTime
            )

        // Simulate the data
        //val response = simulateDerivedDataService(startTime, endTime)

        if (response.success.not()) {
            Log.e("MonitorViewModel", "Error loading stress data: ${response.error}")
            _uiState.update {
                it.copy(
                    stressError = response.error,
                    isStressDataLoading = false
                )
            }
            return
        }
        Log.d(
            "MonitorViewModel",
            "Data loaded successfully. (${response.data?.size} items)"
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

        // Clean data
        if (response.data != null) {
            val data = generateDerivedDataForInterval(
                startTime = startTime,
                endTime = (
                        if(isToday())
                            Calendar.getInstance().timeInMillis
                        else
                            endTime
                        ),
                data = _uiState.value.stressData
            )
            _uiState.update {
                it.copy(
                    stressData = data
                )
            }
        }

        // Set the loading state to false
        _uiState.update {
            it.copy(isStressDataLoading = false)
        }
        Log.d("MonitorViewModel", "Stress data loading finished.")
    }

    /**
     * Function to retrieve physiological data.
     *
     * @param startTime The start time for the data retrieval.
     * @param endTime The end time for the data retrieval.
     */
    private suspend fun retrievePhysiologicalData(startTime: Long, endTime: Long) {

        Log.d("MonitorViewModel", "Loading physiological data...")
        // Retrieve the data
        val response: ServiceResult<List<StressRawData>, StressErrorType> =
            dataService.getRawData(
                startTime = startTime,
                endTime = endTime
            )

        // Simulate the starting data
        /*val response: ServiceResult<List<StressRawData>, StressErrorType> =
            retrieveDailySimulatedSata()*/

        // Simulate a network physiologicalError
        // val startingData: ServiceResult<List<StressRawData>, StressErrorType> =
        //    networkErrorSimulation()

        if (response.success.not()) {
            Log.e("MonitorViewModel", "Error loading physiological data: ${response.error}")
            _uiState.update {
                it.copy(
                    physiologicalError = response.error,
                    isPhysiologicalDataLoading = false
                )
            }
            return
        }
        Log.d(
            "MonitorViewModel",
            "Data loaded successfully. (${response.data?.size} items)"
        )

        // Order by timestamp
        response.data?.sortedBy { it.timestamp }

        // Update the UI state with the starting data
        _uiState.update {
            it.copy(
                physiologicalData = response.data?.map { rawData ->
                    FormattedStressRawData(
                        millis = rawData.timestamp,
                        skinTemperatureSensor = rawData.skinTemperatureSensor,
                        edaSensor = rawData.edaSensor,
                        heartRateSensor = rawData.heartRateSensor
                    )
                } ?: emptyList(),
                physiologicalError = response.error
            )
        }

        // Clean data
        if (response.data != null) {
            val data = generateRawDataForInterval(
                startTime = startTime,
                endTime = (
                    if(isToday())
                        Calendar.getInstance().timeInMillis
                    else
                        endTime
                ),
                data = _uiState.value.physiologicalData
            )
            _uiState.update {
                it.copy(
                    physiologicalData = data
                )
            }
        }

        // Create a map where the key is the field name and the value is a list of pairs (timestamp, field value)
        temporalMapping()

        // Set the loading state to false
        _uiState.update {
            it.copy(isPhysiologicalDataLoading = false)
        }
        Log.d("MonitorViewModel", "Physiological data loading finished.")
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
//                step = 6000,
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

    /**
     * Function to map the physiological data over time.
     *
     * This function creates a map where the key is the field name and the value is a list of pairs (timestamp, field value).
     * It filters out fields that are not needed and populates the map with the physiological data.
     */
    private fun temporalMapping() {

        // Filter fields that are not needed
        val filter = { field: java.lang.reflect.Field ->
            field.name != "millis" && field.name != "\$stable" && field.name != "timestamp" && field.name != "dummy"
        }

        val map = FormattedStressRawData::class.java.declaredFields
            .filter { filter(it) }
            .associateBy({ it.name }, { mutableListOf<Pair<String, Any>>() })

        _uiState.value.physiologicalData.forEach { data ->
            FormattedStressRawData::class.java.declaredFields
                .filter { filter(it) }
                .forEach { field ->
                    field.isAccessible = true
                    val value = field.get(data)
                    if (value != null) {
                        map[field.name]?.add(Pair(data.timestamp, value))
                    }
                }
        }

        _uiState.update {
            it.copy(
                physiologicalMappedData = map
            )
        }

        Log.d("MonitorViewModel", "Temporal mapping finished: ${_uiState.value.physiologicalMappedData}")
    }

    /**
     * Function to generate raw data for a given interval.
     *
     * @param startTime The start time for the data generation.
     * @param endTime The end time for the data generation.
     * @param data The data to be used for the generation.
     */
    private fun generateRawDataForInterval(startTime: Long, endTime: Long, data: List<FormattedStressRawData>): List<FormattedStressRawData> {

        val minHeartRateSensor = data.minOfOrNull { it.heartRateSensor } ?: 0f
        val minSkinTemperatureSensor = data.minOfOrNull { it.skinTemperatureSensor } ?: 0f
        val minEdaSensor = 0f
        val result = mutableListOf<FormattedStressRawData>()

        for (time in startTime until endTime step PHYSIO_STEP_SIZE) {
            val dataInSecond = data.filter { it.millis in time until time + PHYSIO_STEP_SIZE }

            if (dataInSecond.isNotEmpty()) {
                val heartRateSensorAvg = dataInSecond.map { it.heartRateSensor }.average()
                val skinTemperatureSensorAvg = dataInSecond.map { it.skinTemperatureSensor }.average()
                val edaSensorAvg = dataInSecond.map { it.edaSensor }.average()

                result.add(
                    FormattedStressRawData(
                        millis = time,
                        heartRateSensor = heartRateSensorAvg.toFloat(),
                        skinTemperatureSensor = skinTemperatureSensorAvg.toFloat(),
                        edaSensor = edaSensorAvg.toFloat()
                    )
                )
            } else {
                result.add(
                    FormattedStressRawData(
                        millis = time,
                        heartRateSensor = minHeartRateSensor,
                        skinTemperatureSensor = minSkinTemperatureSensor,
                        edaSensor = minEdaSensor,
                        dummy = true
                    )
                )
            }
        }

        Log.d("MonitorViewModel", "Generated data for interval: $result")
        return result
    }

    /**
     * Function to generate derived data for a given interval.
     *
     * @param startTime The start time for the data generation.
     * @param endTime The end time for the data generation.
     * @param data The data to be used for the generation.
     */
    private fun generateDerivedDataForInterval(startTime: Long, endTime: Long, data: List<FormattedStressDerivedData>) : List<FormattedStressDerivedData> {
        val result = mutableListOf<FormattedStressDerivedData>()

        for (time in startTime until endTime step STRESS_STEP_SIZE) {
            val dataInSecond = data.filter { it.millis in time until time + STRESS_STEP_SIZE }

            if (dataInSecond.isNotEmpty()) {
                val weightedAverage =
                    (dataInSecond.sumOf { it.stressLevel.toDouble().pow(STRESS_IMPORTANCE) } /
                            dataInSecond.sumOf { it.stressLevel.toDouble().pow(STRESS_IMPORTANCE - 1) })
                val lowerBoundAvg = dataInSecond.map { it.lowerBound }.average()
                val upperBoundAvg = dataInSecond.map { it.upperBound }.average()

                result.add(
                    FormattedStressDerivedData(
                        millis = time,
                        stressLevel = weightedAverage.toFloat(),
                        lowerBound = lowerBoundAvg.toFloat(),
                        upperBound = upperBoundAvg.toFloat()
                    )
                )
            } else {
                result.add(
                    FormattedStressDerivedData(
                        millis = time,
                        stressLevel = 0f,
                        lowerBound = 0f,
                        upperBound = 0f,
                        dummy = true
                    )
                )
            }
        }

        return result
    }

    /**
     * Function to switch to the previous day.
     */
    fun previousDay() {
        _uiState.update {
            it.copy(
                day = Calendar.getInstance().apply {
                    time = it.day
                    add(Calendar.DAY_OF_MONTH, -1)
                }.time
            )
        }
        retrieveData()
    }

    /**
     * Function to switch to the next day.
     */
    fun nextDay() {
        _uiState.update {
            it.copy(day = Calendar.getInstance().apply {
                time = it.day
                add(Calendar.DAY_OF_MONTH, 1)
            }.time)
        }
        retrieveData()
    }

    /**
     * Function to switch to the current day.
     */
    fun currentDay() {
        _uiState.update {
            it.copy(day = Calendar.getInstance().time)
        }
        retrieveData()
    }

    /**
     * Function to check if the current day is today.
     *
     * @return A boolean indicating whether the current day is today.
     */
    fun isToday(): Boolean {
        return Calendar.getInstance().apply {
            time = _uiState.value.day
        }.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Function to format the current date.
     *
     * @return A string representing the formatted date.
     */
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

