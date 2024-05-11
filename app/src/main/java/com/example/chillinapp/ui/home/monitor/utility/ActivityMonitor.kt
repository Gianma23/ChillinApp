package com.example.chillinapp.ui.home.monitor.utility

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.Point
import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.ui.stressErrorText
import java.util.Locale


@Composable
fun ActivityMonitor(
    mappedData: List<Map.Entry<String, List<Pair<String, Any>>>>,
    data: List<FormattedStressRawData>,
    isPhysiologicalDataLoading: Boolean,
    physiologicalError: StressErrorType?,
    titleFormatMap: Map<String, String>,
) {
    Text(
        text = "Activity",
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Start
    )

    for (entry in mappedData)
        PhysioMonitorCard(
            titleFormatMap = titleFormatMap,
            entry = entry,
            data = data,
            isPhysiologicalDataLoading = isPhysiologicalDataLoading,
            physiologicalError = physiologicalError
        )
}


@Composable
private fun PhysioMonitorCard(
    titleFormatMap: Map<String, String>,
    entry: Map.Entry<String, List<Pair<String, Any>>>,
    data: List<FormattedStressRawData>,
    isPhysiologicalDataLoading: Boolean,
    physiologicalError: StressErrorType?,
) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val titleFormatFunction = titleFormatMap[entry.key]
            Text(
                text = titleFormatFunction ?: entry.key,
                style = MaterialTheme.typography.labelLarge
            )

            val points = entry.value.map { (timestamp, value) ->
                data.find { it.timestamp == timestamp }?.let {
                    Triple(
                        Point(timestampToHourOfDay(timestamp), value as Float),
                        timestampToMillis(timestamp),
                        it.dummy
                    )
                }
            }

            PhysioCardContent(
                isPhysiologicalDataLoading = isPhysiologicalDataLoading,
                physiologicalError = physiologicalError,
                points = points,
            )

        }
    }
}

@Composable
private fun PhysioCardContent(
    isPhysiologicalDataLoading: Boolean,
    physiologicalError: StressErrorType?,
    points: List<Triple<Point, Long, Boolean>?>,
) {
    when {
        isPhysiologicalDataLoading -> {
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

        physiologicalError != null -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Error: ${stressErrorText(physiologicalError)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }

        points.isEmpty() -> {
            Log.d("MonitorScreen", "No data")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No data",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center
                )
            }
        }

        else -> {
            Log.d("MonitorScreen", "Loaded data")
            DataLineChart(
                points = points,
                xLabelFun = { if (it < 10) "          0$it:00" else "          $it:00" },
                yLabelFun = { step ->
                    val yScale = (points.mapNotNull { it?.first }.maxOf { it.y } - points.mapNotNull { it?.first }.minOf { it.y }) / (3)
                    val value = step * yScale + points.mapNotNull { it?.first }.minOf { it.y }
                    when {
                        value >= 100 -> "${value.toInt()}   "
                        value >= 10 -> "${"%.1f".format(Locale.getDefault(), value)}    "
                        value >= 1 -> "${"%.2f".format(Locale.getDefault(), value)}   "
                        value == 0f -> "0   "
                        else -> "${"%.3f".format(Locale.getDefault(), value)}   "
                    }
                }
            )
        }
    }
}