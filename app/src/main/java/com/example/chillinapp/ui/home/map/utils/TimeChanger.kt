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

/**
 * A Composable function that represents a time changer component.
 *
 * @param previousHour A function that is invoked when the "previous hour" button is clicked.
 * @param nextHour A function that is invoked when the "next hour" button is clicked.
 * @param formatTime A function that returns the current time as a formatted string.
 * @param isCurrentHour A function that returns a boolean indicating whether the current time is the current hour.
 * @param stressDataResponse A nullable Boolean that indicates whether there is stress data response available.
 * @param modifier A Modifier that can be used to adjust the layout or other visual properties of the Composable.
 */
@Composable
internal fun TimeChanger(
    previousHour: () -> Unit,
    nextHour: () -> Unit,
    formatTime: () -> String,
    isCurrentHour: () -> Boolean,
    stressDataResponse: Boolean?,
    modifier: Modifier = Modifier
) {

    // A column layout that centers its children horizontally.
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // A button that triggers the action to go to the previous hour.
        Button(
            onClick = { previousHour() },
            enabled = stressDataResponse != null,
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
                text = formatTime(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(4.dp),
                textAlign = TextAlign.Center
            )
        }

        // A button that triggers the action to go to the next hour.
        Button(
            onClick = { nextHour() },
            enabled = stressDataResponse != null && !isCurrentHour(),
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