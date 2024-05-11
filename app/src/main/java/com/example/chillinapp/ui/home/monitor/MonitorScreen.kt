package com.example.chillinapp.ui.home.monitor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.home.monitor.utility.ActivityMonitor
import com.example.chillinapp.ui.home.monitor.utility.StressMonitor
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme

object MonitorDestination: NavigationDestination {
    override val route: String = "monitor"
    override val titleRes: Int = R.string.overview_title
}

@Composable
fun MonitorScreen(
    modifier: Modifier = Modifier,
    viewModel: MonitorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    // Collect the UI state from the view model
    val uiState by viewModel.uiState.collectAsState()


    val titleFormatMap = mapOf(
        "heartRateSensor" to "Heart Rate",
        "skinTemperatureSensor" to "Skin Temperature",
        "edaSensor" to "Electrodermal Activity"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            DaySwitcher(
                modifier = Modifier
                    .fillMaxWidth(),
                onLeftButtonClick = { viewModel.previousDay() },
                onRightButtonClick = { viewModel.nextDay() },
                rightButtonEnabled = !viewModel.isToday() && !uiState.isPhysiologicalDataLoading && !uiState.isStressDataLoading,
                leftButtonEnabled = !uiState.isPhysiologicalDataLoading && !uiState.isStressDataLoading,
                displayedText = viewModel.formatDate()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {


                StressMonitor(
                    isStressDataLoading = uiState.isStressDataLoading,
                    stressError = uiState.stressError,
                    stressData = uiState.stressData,
                    isToday = { viewModel.isToday() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                ActivityMonitor(
                    titleFormatMap = titleFormatMap,
                    mappedData = uiState.physiologicalMappedData.entries.toList(),
                    data = uiState.physiologicalData,
                    isPhysiologicalDataLoading = uiState.isPhysiologicalDataLoading,
                    physiologicalError = uiState.physiologicalError,
                )

            }
        }

    }
}

@Composable
fun DaySwitcher(
    modifier: Modifier = Modifier,
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {},
    leftButtonEnabled: Boolean = true,
    rightButtonEnabled: Boolean = true,
    displayedText: String = ""
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onLeftButtonClick() },
                enabled = leftButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Previous"
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)

            ) {
                Text(
                    text = displayedText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp)
                )
            }

            Button(
                onClick = { onRightButtonClick() },
                enabled = rightButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent
                ),
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

@Preview(showBackground = true)
@Composable
fun MonitorScreenPreview() {
    ChillInAppTheme {
        MonitorScreen()
    }
}
