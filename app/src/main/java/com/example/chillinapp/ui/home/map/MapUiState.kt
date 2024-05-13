package com.example.chillinapp.ui.home.map

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.map.MapErrorType
import com.google.android.gms.maps.model.TileOverlay
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import java.util.Calendar
import java.util.Date

/**
 * Data class representing the UI state of the map.
 *
 * @property checkingPermissions Boolean indicating if permissions are currently being checked.
 * @property cameraPositionState The current state of the camera position on the map.
 * @property mapUiSettings The current UI settings of the map.
 * @property mapProperties The current properties of the map.
 * @property currentDate The current date being displayed on the map.
 * @property stressDataResponse The current response from the service for the stress data.
 * @property isNotificationVisible Boolean indicating if a notification is currently visible.
 * @property maxStressValue The maximum stress value currently being displayed on the map.
 * @property minStressValue The minimum stress value currently being displayed on the map.
 * @property radius The current radius of the search on the map.
 */
data class MapUiState (

    val checkingPermissions: Boolean = true,

    val cameraPositionState: CameraPositionState = CameraPositionState(),
    val mapUiSettings: MapUiSettings = MapUiSettings(),
    val mapProperties: MapProperties = MapProperties(
        mapType = MapType.HYBRID,
    ),

    val currentDate: Date = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, -1) }.time,
    val stressDataResponse: ServiceResult<List<WeightedLatLng>, MapErrorType>? = null,

    val isNotificationVisible: Boolean = false,

    val maxStressValue: Int? = null,
    val minStressValue: Int? = null,
    val radius: Double = 0.0,

    val previousPoints: List<WeightedLatLng> = emptyList(),
    val provider: HeatmapTileProvider? = null,
    val tileOverlay: TileOverlay? = null

    )