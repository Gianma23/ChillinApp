package com.example.chillinapp.ui.home.monitor.utility

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.ui.stressErrorText
import java.util.Locale

@Composable
fun PhysioMonitorCard(
    titleFormatMap: Map<String, String>,
    entry: Map.Entry<String, List<Pair<String, Any>>>,
    isPhysiologicalDataLoading: Boolean,
    physiologicalError: StressErrorType?
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
                Point(timestampToHourOfDay(timestamp), value as Float)
            }

            PhysioCardContent(
                isPhysiologicalDataLoading = isPhysiologicalDataLoading,
                physiologicalError = physiologicalError,
                points = points
            )

        }
    }
}

@Composable
private fun PhysioCardContent(
    isPhysiologicalDataLoading: Boolean,
    physiologicalError: StressErrorType?,
    points: List<Point>
) {
    when {
        isPhysiologicalDataLoading -> {
            Log.d("MonitorScreen", "Loading data...")
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
            Log.e("MonitorScreen", "Error loading data: $physiologicalError")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Card (
                    modifier = Modifier
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: ${stressErrorText(physiologicalError)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
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
            DailyPhysioChart(
                points = points,
                xLabelFun = { if (it < 10) "          0$it:00" else "          $it:00" },
                yLabelFun = { step ->
                    val yScale = (points.maxOf { it.y } - points.minOf { it.y }) / (3)
                    val value = step * yScale + points.minOf { it.y }
                    when {
                        value >= 100 -> "${value.toInt()}   "
                        value >= 10 -> "${"%.1f".format(Locale.getDefault(), value)}   "
                        value >= 1 -> "${"%.2f".format(Locale.getDefault(), value)}   "
                        else -> "${"%.3f".format(Locale.getDefault(), value)}   "
                    }
                }
            )
        }
    }
}

@Composable
private fun DailyPhysioChart(
    points: List<Point>,
    xLabelFun: (Int) -> String,
    yLabelFun: (Int) -> String
) {
    val xAxisData = AxisData.Builder()
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(8.dp)
        .steps(24)
        .axisStepSize(100.dp)
        .labelData(xLabelFun)
        .build()

    val yAxisData = AxisData.Builder()
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(8.dp)
        .steps(3)
        .labelData(yLabelFun)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = points,
                    LineStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        lineType = LineType.SmoothCurve(isDotted = false),
                        width = 5f
                    ),
                    IntersectionPoint(
                        color = Color.Transparent,
                    ),
                    SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.tertiary,
                        radius = 3.dp
                    ),
                    ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.inversePrimary,
                                Color.Transparent
                            )
                        )
                    ),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface,
        gridLines = GridLines(color = MaterialTheme.colorScheme.outlineVariant)
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        lineChartData = lineChartData
    )
}