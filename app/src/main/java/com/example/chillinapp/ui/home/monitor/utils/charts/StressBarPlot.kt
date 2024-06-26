package com.example.chillinapp.ui.home.monitor.utils.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import co.yml.charts.common.model.Point
import com.example.chillinapp.R
import com.example.chillinapp.ui.home.monitor.MonitorViewModel
import java.util.Locale

/**
 * A Composable function that represents a bar plot for displaying stress data.
 *
 * @param points The points to be displayed on the bar plot.
 * @param isToday A function that returns true if the current date is today.
 * @param modifier The modifier to be applied to the bar plot, default value is Modifier.
 */
@Composable
fun StressBarPlot(
    points: List<Point>,
    isToday: () -> Boolean,
    modifier: Modifier = Modifier
) {

    // Define the height of the bar plot.
    val height = 250.dp

    // Remember the scroll state of the bar plot.
    val scrollState = rememberScrollState()

    // Define the layout of the bar plot.
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(275.dp),
            contentAlignment = Alignment.BottomStart
        ){

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ){

                Text(
                    text = stringResource(
                        R.string.stress_threshold_label,
                        MonitorViewModel.STRESS_THRESHOLD
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.8f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(height * MonitorViewModel.STRESS_THRESHOLD))

                Text(
                    text = "",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )

            }

            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.Bottom
            ) {

                val barsPerHour = (60 * 60 * 1000 / MonitorViewModel.STRESS_STEP_SIZE).toInt()

                // Box column for each point
                for ((index, point) in points.withIndex()) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(if (point.y == 0f) 1.dp else point.y * height)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (point.y < MonitorViewModel.STRESS_THRESHOLD)
                                        MaterialTheme.colorScheme.onTertiary
                                    else
                                        MaterialTheme.colorScheme.tertiary
                                ),
                        )

                        Text(
                            text =
                            if(index % barsPerHour == 0)
                                String.format(Locale.getDefault(), "%02d:00", index / barsPerHour)
                            else
                                "",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                    }
                }

                LaunchedEffect(Unit) {
                    if (isToday())
                        scrollState.scrollTo(scrollState.maxValue)
                }

            }

        }

        Text(
            text = stringResource(R.string.stress_bar_char_description),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start
        )

    }

}

