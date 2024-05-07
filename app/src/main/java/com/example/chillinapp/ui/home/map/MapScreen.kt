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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
    ) {

        when (uiState.checkingPermissions) {
            true -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
            false -> {
                GoogleMap(
                    modifier = modifier.fillMaxSize(),
                    uiSettings = uiState.mapUiSettings,
                    properties = uiState.mapProperties,
                    cameraPositionState = uiState.cameraPositionState
                )
            }
        }

        DisappearingScaleBar(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd),
            cameraPositionState = uiState.cameraPositionState
        )

        Column {
            Button(
                onClick = { viewModel.reloadHeatmap(uiState.cameraPositionState.position.target) },
                modifier = Modifier
                    .padding(16.dp)
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

            ){
                Text(
                    text = viewModel.formatDate(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp)
                )
            }
            Row(
                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
            ) {
                Button(
                    onClick = { viewModel.previousDay() },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Previous"
                    )
                }
                Button(
                    onClick = { viewModel.nextDay() },
                    enabled = !viewModel.isToday(),
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