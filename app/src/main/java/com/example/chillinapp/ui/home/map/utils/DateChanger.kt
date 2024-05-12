package com.example.chillinapp.ui.home.map.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.chillinapp.R
import com.example.chillinapp.ui.home.map.MapUiState
import com.example.chillinapp.ui.home.map.MapViewModel

/**
 * A Composable function that provides a UI for changing the date.
 *
 * @param viewModel The ViewModel that contains the business logic for the map.
 * @param uiState The current state of the UI.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
internal fun DateChanger(
    viewModel: MapViewModel,
    uiState: MapUiState,
    modifier: Modifier = Modifier
) {

    // A row layout that aligns its children vertically in the center.
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // A button that triggers the action to go to the previous day.
        Button(
            onClick = { viewModel.previousDay() },
            enabled = uiState.stressDataResponse != null,
            modifier = Modifier.padding(8.dp)
        ) {
            // An icon representing the "previous" action.
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = stringResource(R.string.previous_day_content_descr)
            )
        }

        // A box that displays the current date.
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // The text view that displays the formatted date.
            Text(
                text = viewModel.formatDate(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(4.dp)
            )
        }

        // A button that triggers the action to go to the next day.
        Button(
            onClick = { viewModel.nextDay() },
            enabled = !viewModel.isToday() && uiState.stressDataResponse != null,
            modifier = Modifier.padding(8.dp)
        ) {
            // An icon representing the "next" action.
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = stringResource(R.string.next_day_content_descr)
            )
        }

    }
}