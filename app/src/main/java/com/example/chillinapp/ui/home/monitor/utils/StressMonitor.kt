package com.example.chillinapp.ui.home.monitor.utils

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.Point
import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.ui.stressErrorText

/**
 * A Composable function that represents a stress monitor.
 *
 * @param modifier The modifier to be applied to the stress monitor, default value is Modifier.
 * @param isStressDataLoading A boolean flag indicating whether the stress data is loading.
 * @param stressError The error type of the stress data, if any.
 * @param isToday A function that returns true if the current date is today.
 * @param stressData The stress data to be displayed on the stress monitor.
 */
@Composable
fun StressMonitor(
    modifier: Modifier = Modifier,
    isStressDataLoading: Boolean,
    stressError: StressErrorType?,
    isToday: () -> Boolean,
    stressData: List<FormattedStressDerivedData>,
) {

    // Display the title of the stress monitor.
    Text(
        text = "Stress level",
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Start
    )

    // Define the layout of the stress monitor.
    Box(
        modifier = modifier
            .background(Color.Transparent)
            .fillMaxWidth()
    ) {

        // Log the loading state of the stress data.
        Log.d("MonitorScreen", "isStressLoading: $isStressDataLoading")
        when {

            // If the stress data is loading, display a circular progress indicator.
            isStressDataLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            // If there is an error in the stress data, display the error message.
            stressError != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Error: ${stressErrorText(stressError)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // If the stress data is available, display the stress data.
            else -> {

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){

                    // Convert the stress data to points for the bar plot.
                    val points: List<Point> = stressData.map { data ->
                        Point(data.millis.toFloat(), data.stressLevel)
                    }

                    // Display the stress data on a bar plot.
                    StressBarPlot(
                        points = points,
                        isToday = isToday
                    )
                    // Display the stress data on a donut chart.
                    StressDonutChart(
                        stressData = stressData
                    )

                }

            }
        }
    }
}