package com.example.chillinapp.ui.home.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.map.MapErrorType
import com.example.chillinapp.data.map.MapService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MapViewModel(
    private val mapService: MapService
) : ViewModel() {

    // Mutable state flow for the UI state of the map screen
    private val _uiState = MutableStateFlow(MapUiState())

    // State flow for the UI state of the map screen
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    companion object {
        // Default location (Polo A - Engineering University of Pisa)
        private val DEFAULT_LOCATION = LatLng(43.72180384669495, 10.389285990216196)

        // Default radius for which provide stress data
        private const val DEFAULT_RADIUS = 0.01
    }

    fun checkPermissions(context: Context) {

        val permissionGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        Log.d("MapViewModel", "Permission granted: $permissionGranted")

        if (!permissionGranted) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
        Log.d("MapViewModel", "Requesting permission")

        getLastLocation(context)

    }

    private fun getLastLocation(context: Context) {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MapViewModel", "No permission")
            _uiState.value = MapUiState(
                cameraPositionState = CameraPositionState(
                    CameraPosition.fromLatLngZoom(
                        DEFAULT_LOCATION,
                        15f
                    )
                ),
                checkingPermissions = false
            )
            Log.d("MapViewModel", "Location: $DEFAULT_LOCATION")

            return
        }

        Log.d("MapViewModel", "Permission granted")

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _uiState.value = MapUiState(
                    cameraPositionState = CameraPositionState(
                        CameraPosition.fromLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            15f
                        )
                    ),
                    checkingPermissions = false
                )
            } else {
                _uiState.value = MapUiState(
                    cameraPositionState = CameraPositionState(
                        CameraPosition.fromLatLngZoom(
                            DEFAULT_LOCATION,
                            15f
                        )
                    ),
                    checkingPermissions = false
                )
            }
            Log.d("MapViewModel", "Location: $location")

        }
    }

    fun loadHeatPoints(target: LatLng) {

        Log.d("MapViewModel", "Reloading heatPoints, target: $target")
        _uiState.update { it.copy(
            stressDataResponse = null
        ) }

        viewModelScope.launch {

            val response: ServiceResult<List<WeightedLatLng>, MapErrorType> = mapService.get(
                centerLat = target.latitude,
                centerLong = target.longitude,
                distance = DEFAULT_RADIUS,
                date = uiState.value.currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                hour = uiState.value.currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().hour
            )

            // Simulate stressData
//            delay(2000)
//            val response = simulateStressDataService(
//                center = target,
//                radius = DEFAULT_RADIUS,
//                date = uiState.value.currentDate
//            )
//            Log.d("MapViewModel", "Response: $response")

            // Update UI state
            _uiState.value = _uiState.value.copy(
                stressDataResponse = response
            )

            // Show notification if error or no data
            if (response.error != null || response.data.isNullOrEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isNotificationVisible = true
                )
            }

            // Update min and max stress values
            if (response.data != null) {
                val max = response.data.maxByOrNull { it.intensity }?.intensity?.toInt()
                val min = response.data.minByOrNull { it.intensity }?.intensity?.toInt()
                _uiState.value = _uiState.value.copy(
                    maxStressValue = max,
                    minStressValue = min
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    maxStressValue = null,
                    minStressValue = null
                )
            }

            Log.d("MapViewModel", "Stress Points loaded")
        }

    }

    fun previousDay() {
        val calendar = Calendar.getInstance()
        calendar.time = uiState.value.currentDate
        calendar.add(Calendar.DAY_OF_MONTH, -1)

        _uiState.value = _uiState.value.copy(currentDate = calendar.time)
        Log.d("MapViewModel", "Current date: ${uiState.value.currentDate}")
    }

    fun nextDay() {
        val calendar = Calendar.getInstance()
        calendar.time = uiState.value.currentDate
        calendar.add(Calendar.DAY_OF_MONTH, 1)

        val now = Calendar.getInstance()

        // Hour check
        if (isSameDay(calendar.time, now.time) && calendar.get(Calendar.HOUR_OF_DAY) > now.get(Calendar.HOUR_OF_DAY)) {
            calendar.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY))
        }

        _uiState.value = _uiState.value.copy(currentDate = calendar.time)
        Log.d("MapViewModel", "Current date: ${uiState.value.currentDate}")
    }

    fun formatDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            uiState.value.currentDate
        ).toString()
    }

    fun isToday(): Boolean {
        return isSameDay(Date(), uiState.value.currentDate)
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.time = date1
        val calendar2 = Calendar.getInstance()
        calendar2.time = date2
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    fun hideNotifyAction() {
        _uiState.value = _uiState.value.copy(isNotificationVisible = false)
    }

    fun updateCameraPosition(target: LatLng) {
        _uiState.value = _uiState.value.copy(
            cameraPositionState = CameraPositionState(
                CameraPosition.fromLatLngZoom(
                    target,
                    uiState.value.cameraPositionState.position.zoom
                )
            )
        )
    }

    fun previousHour() {
        val calendar = Calendar.getInstance()
        calendar.time = uiState.value.currentDate
        calendar.add(Calendar.HOUR_OF_DAY, -1)

        _uiState.value = _uiState.value.copy(currentDate = calendar.time)
        Log.d("MapViewModel", "Current date: ${uiState.value.currentDate}")
    }

    fun formatTime(): String {
        val currentHour = SimpleDateFormat("HH:00", Locale.getDefault()).format(uiState.value.currentDate)

        val calendar = Calendar.getInstance()
        calendar.time = uiState.value.currentDate
        calendar.add(Calendar.HOUR_OF_DAY, 1)
        val nextHour = SimpleDateFormat("HH:00", Locale.getDefault()).format(calendar.time)

        return "$currentHour\n-\n$nextHour"
    }

    fun nextHour() {
        val calendar = Calendar.getInstance()
        calendar.time = uiState.value.currentDate
        calendar.add(Calendar.HOUR_OF_DAY, 1)

        _uiState.value = _uiState.value.copy(currentDate = calendar.time)
        Log.d("MapViewModel", "Current date: ${uiState.value.currentDate}")
    }

    fun isCurrentHour(): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = uiState.value.currentDate
        return calendar.get(Calendar.HOUR_OF_DAY) == Calendar.getInstance().get(Calendar.HOUR_OF_DAY) &&
                isSameDay(calendar.time, Date())
    }

}