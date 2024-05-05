package com.example.chillinapp.ui.home.map

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.google.maps.android.compose.GoogleMap

object MapDestination : NavigationDestination {
    override val route: String = "map"
    override val titleRes: Int = R.string.map_screen_title
}

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(
        factory = AppViewModelProvider.Factory
    )
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionGranted = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        permissionGranted.value = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        Log.d("MapScreen", "Permission granted: ${permissionGranted.value}")

        if (!permissionGranted.value) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
        Log.d("MapScreen", "Requesting permission")

        viewModel.getLastLocation(context)
    }


    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = uiState.cameraPositionState
    )

}

@Preview
@Composable
fun MapScreenPreview() {
    MapScreen()
}