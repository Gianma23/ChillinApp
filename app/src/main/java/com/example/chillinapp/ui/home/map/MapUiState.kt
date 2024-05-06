package com.example.chillinapp.ui.home.map

import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings

data class MapUiState (

    val checkingPermissions: Boolean = true,

    val cameraPositionState: CameraPositionState = CameraPositionState(),
    val mapUiSettings: MapUiSettings = MapUiSettings(),
    val mapProperties: MapProperties = MapProperties(
        mapType = MapType.HYBRID,
    )

)