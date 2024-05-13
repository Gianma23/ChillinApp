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
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.home.monitor.utils.ActivityMonitor
import com.example.chillinapp.ui.home.monitor.utils.StressMonitor
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme

/**
 * Object representing the monitor destination in the navigation.
 */
object MonitorDestination: NavigationDestination {
    override val route: String = "monitor"
    override val titleRes: Int = R.string.overview_title
}

/**
 * A Composable function that represents the monitor screen.
 *
 * @param modifier The modifier to be applied to the monitor screen, default value is Modifier.
 * @param viewModel The view model for the monitor screen, default value is the view model from the AppViewModelProvider factory.
 */
@Composable
fun MonitorScreen(
    modifier: Modifier = Modifier,
    viewModel: MonitorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    // Collect the UI state from the view model
    val uiState by viewModel.uiState.collectAsState()

    // Map of sensor names to their display names
    val titleFormatMap = mapOf(
        "heartRateSensor" to stringResource(R.string.heart_rate_label),
        "skinTemperatureSensor" to stringResource(R.string.skin_temperature_label),
        "edaSensor" to stringResource(R.string.electrodermal_activity_label)
    )

    // Define the layout of the monitor screen
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // Define the day switcher
            DaySwitcher(
                modifier = Modifier
                    .fillMaxWidth(),
                onLeftButtonClick = { viewModel.previousDay() },
                onRightButtonClick = { viewModel.nextDay() },
                onCurrentDayClick = { viewModel.currentDay() },
                reload = { viewModel.retrieveData() },
                rightButtonEnabled = !viewModel.isToday() && !uiState.isPhysiologicalDataLoading && !uiState.isStressDataLoading,
                leftButtonEnabled = !uiState.isPhysiologicalDataLoading && !uiState.isStressDataLoading,
                displayedText = viewModel.formatDate()
            )

            // Define the layout of the stress monitor and activity monitor
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {

                // Define the stress monitor
                StressMonitor(
                    isStressDataLoading = uiState.isStressDataLoading,
                    stressError = uiState.stressError,
                    stressData = uiState.stressData,
                    isToday = { viewModel.isToday() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Define the activity monitor
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

/**
 * A Composable function that represents a day switcher.
 *
 * @param modifier The modifier to be applied to the day switcher, default value is Modifier.
 * @param onLeftButtonClick The function to be executed when the left button is clicked, default value is an empty function.
 * @param onRightButtonClick The function to be executed when the right button is clicked, default value is an empty function.
 * @param leftButtonEnabled A boolean flag indicating whether the left button is enabled, default value is true.
 * @param rightButtonEnabled A boolean flag indicating whether the right button is enabled, default value is true.
 * @param displayedText The text to be displayed in the day switcher, default value is an empty string.
 */
@Composable
fun DaySwitcher(
    modifier: Modifier = Modifier,
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {},
    onCurrentDayClick: () -> Unit = {},
    reload: () -> Unit = {},
    leftButtonEnabled: Boolean = true,
    rightButtonEnabled: Boolean = true,
    displayedText: String = ""
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { onLeftButtonClick() },
                enabled = leftButtonEnabled,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = stringResource(id = R.string.previous_day_content_descr)
                )
            }

            IconButton(
                onClick = { reload() },
                enabled = leftButtonEnabled,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent
                ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Replay,
                    contentDescription = stringResource(R.string.reload_content_descr)
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .align(Alignment.Center)
        ) {
            Text(
                text = displayedText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(4.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { onCurrentDayClick() },
                enabled = rightButtonEnabled,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent
                ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = stringResource(R.string.current_day_content_descr)
                )
            }

            IconButton(
                onClick = { onRightButtonClick() },
                enabled = rightButtonEnabled,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = Color.Transparent
                ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = stringResource(id = R.string.next_day_content_descr)
                )
            }
        }
    }
}

/**
 * A Composable function that represents a preview of the monitor screen.
 */
@Preview(showBackground = true)
@Composable
fun MonitorScreenPreview() {
    ChillInAppTheme {
        MonitorScreen()
    }
}