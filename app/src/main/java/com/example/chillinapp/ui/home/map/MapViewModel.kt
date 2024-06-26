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
import com.google.android.gms.maps.model.TileOverlay
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.pow

/**
 * ViewModel for the Map screen.
 *
 * @property mapService Service for fetching map data.
 */
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

        // Radius values
        private const val MIN_ZOOM = 5.0f
        private const val MAX_ZOOM = 17.5f
        private const val MIN_RADIUS = 0.08
        private const val MAX_RADIUS = 20.0
    }

    /**
     * Updates the radius based on the zoom level.
     *
     * @param zoom The current zoom level.
     */
    fun updateRadius(zoom: Float) {
        when {
            zoom < MAX_ZOOM && zoom > MIN_ZOOM -> {
                val a = MAX_RADIUS
                val b =
                    (MIN_RADIUS / MAX_RADIUS).pow((1 / (MAX_ZOOM - MIN_ZOOM)).toDouble())
                val radius = (a * b.pow(zoom.toDouble())).toFloat()
                _uiState.value = _uiState.value.copy(
                    radius = radius.toDouble()
                )
            }
            else -> { }
        }
    }

    /**
     * Checks if the necessary permissions are granted and requests them if not.
     *
     * @param context The context to use for checking and requesting permissions.
     */
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

    /**
     * Gets the last known location of the device.
     *
     * @param context The context to use for getting the location.
     */
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
            _uiState.update {
                it.copy(
                    checkingPermissions = false,
                    cameraPositionState = CameraPositionState(
                        CameraPosition.fromLatLngZoom(
                            DEFAULT_LOCATION,
                            15f
                        )
                    )
                )
            }
            Log.d("MapViewModel", "Location: $DEFAULT_LOCATION")

            return
        }

        Log.d("MapViewModel", "Permission granted")

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _uiState.update {
                    it.copy(
                        cameraPositionState = CameraPositionState(
                            CameraPosition.fromLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                15f
                            )
                        ),
                        checkingPermissions = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        cameraPositionState = CameraPositionState(
                            CameraPosition.fromLatLngZoom(
                                DEFAULT_LOCATION,
                                15f
                            )
                        ),
                        checkingPermissions = false
                    )
                }
            }
            Log.d("MapViewModel", "Location: $location")

        }
    }

    /**
     * Loads the stress points for the given target location.
     *
     * @param target The target location for which to load the stress points.
     */
    fun loadHeatPoints(target: LatLng) {

        hideNotifyAction()

        Log.d("MapViewModel", "Reloading heatPoints, target: $target")
        _uiState.update { it.copy(
            stressDataResponse = null
        ) }

        viewModelScope.launch {

            Log.d("MapViewModel", "Loading stress points for:")
            Log.d("MapViewModel", "Latitude: ${target.latitude}")
            Log.d("MapViewModel", "Longitude: ${target.longitude}")
            Log.d("MapViewModel", "Radius: ${uiState.value.radius}")
            Log.d("MapViewModel", "Date: ${uiState.value.currentDate.toInstant().atZone(ZoneOffset.UTC).toLocalDate()}")
            Log.d("MapViewModel", "Hour: ${uiState.value.currentDate.toInstant().atZone(ZoneOffset.UTC).toLocalTime().hour}")

            // Load stress points
            val response: ServiceResult<List<WeightedLatLng>, MapErrorType> = mapService.get(
                centerLat = target.latitude,
                centerLong = target.longitude,
                distance = uiState.value.radius,
                date = uiState.value.currentDate.toInstant().atZone(ZoneOffset.UTC).toLocalDate(),
                hour = uiState.value.currentDate.toInstant().atZone(ZoneOffset.UTC).toLocalTime().hour
            )

            // Simulate physiologicalData
//            delay(2000)
//            val response = simulateStressDataService(
//                center = target,
//                radius = uiState.value.radius,
//                date = uiState.value.currentDate
//            )
            Log.d("MapViewModel", "Response: $response")
            if(response.error == null) {
                Log.d("MapViewModel", "Data:")
                response.data?.forEach {
                    Log.d("MapViewModel", "Lat: ${it.point.x}, Long: ${it.point.y}, Intensity: ${it.intensity}")
                }
            }

            // Update UI state
            _uiState.value = _uiState.value.copy(
                stressDataResponse = response
            )

            // Show notification if physiologicalError or no data
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

    /**
     * Moves the current date one day back.
     */
    fun previousDay() {
        val calendar = Calendar.getInstance()
        calendar.time = uiState.value.currentDate
        calendar.add(Calendar.DAY_OF_MONTH, -1)

        _uiState.value = _uiState.value.copy(currentDate = calendar.time)
        Log.d("MapViewModel", "Current date: ${uiState.value.currentDate}")
    }

    /**
     * Moves the current date one day forward.
     */
    fun nextDay() {
        val calendar = Calendar.getInstance()
        calendar.time = uiState.value.currentDate
        calendar.add(Calendar.DAY_OF_MONTH, 1)

        val now = Calendar.getInstance()

        // Hour check
        if (isSameDay(calendar.time, now.time) && calendar.get(Calendar.HOUR_OF_DAY) > now.get(Calendar.HOUR_OF_DAY) - 1) {
            calendar.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY) - 1)
        }

        _uiState.value = _uiState.value.copy(currentDate = calendar.time)
        Log.d("MapViewModel", "Current date: ${uiState.value.currentDate}")
    }

    /**
     * Formats the current date to a string.
     *
     * @return The formatted date string.
     */
    fun formatDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            uiState.value.currentDate
        ).toString()
    }

    /**
     * Formats the current time to a string.
     *
     * @return The formatted time string.
     */
    fun formatTime(): String {
        val currentHour = SimpleDateFormat("HH:00", Locale.getDefault()).format(uiState.value.currentDate)

        val calendar = Calendar.getInstance()
        calendar.time = uiState.value.currentDate
        calendar.add(Calendar.HOUR_OF_DAY, 1)
        val nextHour = SimpleDateFormat("HH:00", Locale.getDefault()).format(calendar.time)

        return "$currentHour\n-\n$nextHour"
    }

    /**
     * Checks if the current date is today.
     *
     * @return True if the current date is today, false otherwise.
     */
    fun isToday(): Boolean {
        return isSameDay(Date(), uiState.value.currentDate)
    }

    /**
     * Hides the notification action.
     */
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.time = date1
        val calendar2 = Calendar.getInstance()
        calendar2.time = date2
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Hides the notification action.
     */
    fun hideNotifyAction() {
        _uiState.value = _uiState.value.copy(isNotificationVisible = false)
    }

    /**
     * Updates the camera position.
     *
     * @param cameraPositionState The new camera position state.
     */
    fun updateCameraPosition(cameraPositionState: CameraPositionState) {
        _uiState.value = _uiState.value.copy(
            cameraPositionState = cameraPositionState
        )
    }

    /**
     * Moves the current hour one hour back.
     */
    fun previousHour() {
        val calendar = Calendar.getInstance()
        calendar.time = uiState.value.currentDate
        calendar.add(Calendar.HOUR_OF_DAY, -1)

        _uiState.value = _uiState.value.copy(currentDate = calendar.time)
        Log.d("MapViewModel", "Current date: ${uiState.value.currentDate}")
    }

    /**
     * Moves the current hour one hour forward.
     */
    fun nextHour() {
        val calendar = Calendar.getInstance()
        calendar.time = uiState.value.currentDate
        calendar.add(Calendar.HOUR_OF_DAY, 1)

        _uiState.value = _uiState.value.copy(currentDate = calendar.time)
        Log.d("MapViewModel", "Current date: ${uiState.value.currentDate}")
    }

    /**
     * Checks if the current hour is the previous hour.
     *
     * @return True if the current hour is the previous hour, false otherwise.
     */
    fun isCurrentPreviousHour(): Boolean {
        val now = Calendar.getInstance()

        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentDate = now.get(Calendar.DAY_OF_YEAR)

        val uiStateHour = Calendar.getInstance().apply {
            time = uiState.value.currentDate
        }.get(Calendar.HOUR_OF_DAY)

        val uiStateDate = Calendar.getInstance().apply {
            time = uiState.value.currentDate
        }.get(Calendar.DAY_OF_YEAR)

        return currentHour - 1 == uiStateHour && currentDate == uiStateDate
    }

    /**
     * Updates the points.
     *
     * @param newPoints The new points.
     */
    fun updatePoints(newPoints: List<WeightedLatLng>) {
        _uiState.value = _uiState.value.copy(
            previousPoints = newPoints
        )
    }

    /**
     * Initializes the overlay.
     *
     * @param it The tile overlay.
     */
    fun initializeOverlay(it: TileOverlay?) {
        _uiState.value = _uiState.value.copy(
            tileOverlay = it
        )
    }

    /**
     * Initializes the provider.
     *
     * @param it The heatmap tile provider.
     * @return The heatmap tile provider.
     */
    fun initializeProvider(it: HeatmapTileProvider): HeatmapTileProvider {
        _uiState.value = _uiState.value.copy(
            provider = it
        )
        return it
    }

    /**
     * Clears the overlay.
     */
    fun clearOverlay() {
        _uiState.value.tileOverlay?.remove()
        _uiState.value.tileOverlay?.clearTileCache()
    }

}