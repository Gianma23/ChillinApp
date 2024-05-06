package com.example.chillinapp.ui.home.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.widgets.DisappearingScaleBar

object MapDestination : NavigationDestination {
    override val route: String = "map"
    override val titleRes: Int = R.string.map_screen_title
}

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkPermissions(context)
    }

    when (uiState.checkingPermissions) {
        true -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(color = MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp)
                )
            }
        }
        false -> {
            Box(modifier = modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = modifier.fillMaxSize(),
                    uiSettings = uiState.mapUiSettings,
                    properties = uiState.mapProperties,
                    cameraPositionState = uiState.cameraPositionState
                )

                DisappearingScaleBar(
                    modifier = Modifier
                        .padding(top = 5.dp, end = 15.dp)
                        .align(Alignment.TopEnd),
                    cameraPositionState = uiState.cameraPositionState
                )

                Column {

                }
            }
        }
    }

}

@Preview
@Composable
fun MapScreenPreview() {
    MapScreen()
}