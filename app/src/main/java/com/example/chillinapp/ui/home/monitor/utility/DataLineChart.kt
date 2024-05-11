package com.example.chillinapp.ui.home.monitor.utility

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import java.util.Locale

/**
 * A Composable function that represents a line chart for displaying data.
 *
 * @param points The points to be displayed on the line chart.
 * @param xLabelFun A function that generates the labels for the x-axis.
 * @param yLabelFun A function that generates the labels for the y-axis.
 */
@Composable
fun DataLineChart(
    points: List<Triple<Point, Long, Boolean>?>,
    xLabelFun: (Int) -> String,
    yLabelFun: (Int) -> String
) {

    // Define the x-axis data.
    val xAxisData = AxisData.Builder()
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(8.dp)
        .steps(24)
        .axisStepSize(100.dp)
        .labelData(xLabelFun)
        .build()

    // Define the y-axis data.
    val yAxisData = AxisData.Builder()
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(8.dp)
        .steps(3)
        .labelData(yLabelFun)
        .build()

    // Define the line chart data.
    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = points.mapNotNull { it?.first },
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
                    SelectionHighlightPopUp(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        backgroundAlpha = 0.8f,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        popUpLabel = { x, y ->
                            val timestamp = points.find { it?.first == Point(x, y) }?.second
                            val dummy = points.find { it?.first == Point(x, y) }?.third
                            if(dummy == true)
                                ""
                            else
                                (timestamp?.let { timestampToTime(it) } ?: "") +
                                        ("  value: " + "%.2f".format(Locale.getDefault(), y))
                        }
                    )
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface,
        gridLines = GridLines(color = MaterialTheme.colorScheme.outlineVariant)
    )

    // Create the line chart.
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        lineChartData = lineChartData
    )
}