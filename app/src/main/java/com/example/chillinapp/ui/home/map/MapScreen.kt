package com.example.chillinapp.ui.home.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.SimpleNotification
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.stressErrorText
import com.example.chillinapp.ui.theme.ChillInAppTheme

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
    ) {

        if (!uiState.checkingPermissions) {
            HeatMap(
                cameraPositionState = uiState.cameraPositionState,
                points = uiState.stressDataResponse?.data ?: emptyList(),
                setOnCameraMoveListener = { viewModel.updateCameraPosition(it.position.target) },
                setOnMapLoadedCallback = { viewModel.loadHeatPoints(uiState.cameraPositionState.position.target) }
            )
        }

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
                bodyText = stressErrorText(
                    error =
                    if (uiState.stressDataResponse?.data.isNullOrEmpty()) {
                        StressErrorType.NO_DATA
                    } else {
                        uiState.stressDataResponse?.error
                    }
                )
            )
        }

        Column(
            modifier = Modifier.align(Alignment.TopCenter)

        ) {
            Button(
                onClick = { viewModel.loadHeatPoints(uiState.cameraPositionState.position.target) },
                enabled = uiState.stressDataResponse != null,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text("Reload here")
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)

            ) {
                Text(
                    text = viewModel.formatDate(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp)
                )
            }

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Button(
                    onClick = { viewModel.previousDay() },
                    enabled = uiState.stressDataResponse != null,
                    modifier = Modifier.padding(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Previous"
                    )
                }
                Button(
                    onClick = { viewModel.nextDay() },
                    enabled = !viewModel.isToday() && uiState.stressDataResponse != null,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Next"
                    )
                }
            }
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