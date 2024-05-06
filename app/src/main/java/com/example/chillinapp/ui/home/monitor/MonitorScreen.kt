package com.example.chillinapp.ui.home.monitor

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.chillinapp.R
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme
import java.util.Locale

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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(uiState.fieldValuesMap.entries.toList()) { entry ->
                MonitorCard(titleFormatMap, entry, uiState)
            }
        }
    }
}

@Composable
private fun MonitorCard(
    titleFormatMap: Map<String, String>,
    entry: Map.Entry<String, List<Pair<String, Any>>>,
    uiState: MonitorUiState
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
                style = MaterialTheme.typography.headlineSmall
            )

            val points = entry.value.map { (timestamp, value) ->
                Point(timestampToHourOfDay(timestamp), value as Float)
            }

            CardContent(uiState, points)

        }
    }
}

@Composable
private fun CardContent(
    uiState: MonitorUiState,
    points: List<Point>
) {
    when {
        uiState.isLoading -> {
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

        uiState.error != null -> {
            Log.e("MonitorScreen", "Error loading data: ${uiState.error}")
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
                            text = "Error loading data: ${uiState.error}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        else -> {
            Log.d("MonitorScreen", "Loaded data")
            Chart(
                points = points,
                xSteps = 24,
                ySteps = 3,
                xLabelFun = { if (it < 10) "          0$it:00" else "          $it:00" },
                yLabelFun = { step ->
                    val yScale = (points.maxOf { it.y } - points.minOf { it.y }) / (3)
                    val value = step * yScale + points.minOf { it.y }
                    when {
                        value >= 100 -> "${value.toInt()}   "
                        value >= 10 -> "${"%.1f".format(Locale.US, value)}   "
                        value >= 1 -> "${"%.2f".format(Locale.US, value)}   "
                        else -> "${"%.3f".format(Locale.US, value)}   "
                    }
                }
            )
        }
    }
}

@Composable
private fun Chart(
    points: List<Point>,
    xSteps: Int,
    ySteps: Int,
    xLabelFun: (Int) -> String,
    yLabelFun: (Int) -> String
) {
    val xAxisData = AxisData.Builder()
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(8.dp)
        .steps(xSteps)
        .axisStepSize(100.dp)
        .labelData(xLabelFun)
        .build()

    val yAxisData = AxisData.Builder()
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(8.dp)
        .steps(ySteps)
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

@Preview(showBackground = true)
@Composable
fun MonitorScreenPreview() {
    ChillInAppTheme {
        MonitorScreen()
    }
}
