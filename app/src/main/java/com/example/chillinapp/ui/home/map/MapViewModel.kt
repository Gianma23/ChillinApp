package com.example.chillinapp.ui.home.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel(
//    private val dataService : StressDataService
) : ViewModel() {

    // Mutable state flow for the UI state of the map screen
    private val _uiState = MutableStateFlow(MapUiState())

    // State flow for the UI state of the map screen
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    companion object {
        // Default location (Polo A - Engineering University of Pisa)
        private val DEFAULT_LOCATION = LatLng(43.72180384669495, 10.389285990216196)
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

}