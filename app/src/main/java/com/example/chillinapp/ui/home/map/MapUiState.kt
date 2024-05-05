package com.example.chillinapp.ui.home.map

import com.google.maps.android.compose.CameraPositionState

data class MapUiState (

    val cameraPositionState: CameraPositionState = CameraPositionState(),
    val checkingPermissions: Boolean = true

)