package com.example.chillinapp.ui.home.map.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.chillinapp.ui.home.map.MapUiState
import com.example.chillinapp.ui.home.map.MapViewModel

/**
 * A Composable function that provides a UI for changing the time.
 *
 * @param viewModel The ViewModel that contains the business logic for the map.
 * @param uiState The current state of the UI.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
internal fun TimeChanger(
    viewModel: MapViewModel,
    uiState: MapUiState,
    modifier: Modifier = Modifier
) {

    // A column layout that centers its children horizontally.
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // A button that triggers the action to go to the previous hour.
        Button(
            onClick = { viewModel.previousHour() },
            enabled = uiState.stressDataResponse != null,
            modifier = Modifier.padding(8.dp)
        ) {
            // An icon representing the "previous hour" action.
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Previous Hour"
            )
        }

        // A box that displays the current time.
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.surfaceVariant)

        ) {
            // The text view that displays the formatted time.
            Text(
                text = viewModel.formatTime(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(4.dp),
                textAlign = TextAlign.Center
            )
        }

        // A button that triggers the action to go to the next hour.
        Button(
            onClick = { viewModel.nextHour() },
            enabled = uiState.stressDataResponse != null && !viewModel.isCurrentHour(),
            modifier = Modifier.padding(8.dp)
        ) {
            // An icon representing the "next hour" action.
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Next Hour"
            )
        }

    }

}