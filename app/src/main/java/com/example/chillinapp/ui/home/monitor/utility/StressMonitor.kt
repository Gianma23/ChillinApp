package com.example.chillinapp.ui.home.monitor.utility

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


@Composable
fun StressMonitor(
    modifier: Modifier = Modifier,
    isStressDataLoading: Boolean,
    stressError: StressErrorType?,
    isToday: () -> Boolean,
    stressData: List<FormattedStressDerivedData>,
) {

    Text(
        text = "Stress level",
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Start
    )

    Box(
        modifier = modifier
            .background(Color.Transparent)
            .fillMaxWidth()
    ) {

        Log.d("MonitorScreen", "isStressLoading: $isStressDataLoading")
        when {

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

            else -> {

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){

                    val points: List<Point> = stressData.map { data ->
                        Point(data.millis.toFloat(), data.stressLevel)
                    }

                    StressBarPlot(
                        points = points,
                        isToday = isToday
                    )

                    StressDonutChart(
                        stressData = stressData
                    )

                }

            }
        }
    }
}
