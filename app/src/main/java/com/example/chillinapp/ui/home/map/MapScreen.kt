package com.example.chillinapp.ui.home.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.data.map.MapErrorType
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.SimpleNotification
import com.example.chillinapp.ui.home.map.utils.DateChanger
import com.example.chillinapp.ui.home.map.utils.HeatMap
import com.example.chillinapp.ui.home.map.utils.StressBar
import com.example.chillinapp.ui.home.map.utils.TimeChanger
import com.example.chillinapp.ui.mapErrorText
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme
import com.google.maps.android.heatmaps.Gradient

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

    val colors = intArrayOf(
        android.graphics.Color.rgb(102, 225, 0),  // green
        android.graphics.Color.rgb(255, 0, 0) // red
    )
    val startPoints = floatArrayOf(0.2f, 1f)
    val gradient = Gradient(colors, startPoints)

    LaunchedEffect(Unit) {
        viewModel.checkPermissions(context)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
    ) {

        if (!uiState.checkingPermissions) {
            HeatMap(
                cameraPositionState = uiState.cameraPositionState,
                points = uiState.stressDataResponse?.data ?: emptyList(),
                setOnCameraMoveListener = { viewModel.updateCameraPosition(it) },
                setOnMapLoadedCallback = { viewModel.loadHeatPoints(uiState.cameraPositionState.position.target) },
                updateSearchRadius = { zoom -> viewModel.updateRadius(zoom) },
                gradient = gradient
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .alpha(0.7f)
        ) {
            Button(
                onClick = { viewModel.loadHeatPoints(uiState.cameraPositionState.position.target) },
                enabled = uiState.stressDataResponse != null,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text("Reload")
            }
        }

        TimeChanger(
            viewModel = viewModel,
            uiState = uiState,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterStart)
                .scale(0.9f)
                .alpha(0.7f)
        )

        DateChanger(
            viewModel = viewModel,
            uiState = uiState,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.BottomCenter)
                .alpha(0.7f)
        )

        StressBar(
            gradient = gradient,
            minValue = uiState.minStressValue,
            maxValue = uiState.maxStressValue,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(8.dp)
                .alpha(0.7f)
        )


        if (uiState.stressDataResponse == null || uiState.checkingPermissions) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
            )
        }

        if ((uiState.stressDataResponse?.error != null || uiState.stressDataResponse?.data.isNullOrEmpty())
            && uiState.isNotificationVisible
        ) {
            SimpleNotification(
                action = { viewModel.hideNotifyAction() },
                buttonText = stringResource(id = R.string.hide_notify_action),
                bodyText = mapErrorText(
                    error =
                    if (uiState.stressDataResponse?.data?.isEmpty() == true) {
                        MapErrorType.NO_DATA
                    } else {
                        uiState.stressDataResponse?.error
                    }
                )
            )
        }

    }
}

@Preview
@Composable
fun MapScreenPreview() {
    ChillInAppTheme {
        MapScreen()
    }
}

@Preview
@Composable
fun MapScreenDarkPreview() {
    ChillInAppTheme(useDarkTheme = true) {
        MapScreen()
    }
}