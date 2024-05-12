package com.example.chillinapp.ui.home.monitor.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.chillinapp.ui.home.monitor.MonitorViewModel

/**
 * A Composable function that represents a donut chart for displaying stress data.
 *
 * @param stressData The stress data to be displayed on the donut chart.
 */
@Composable
fun StressDonutChart(stressData: List<FormattedStressDerivedData>) {

    // Define the slices of the donut chart.
    val slices = listOf(
        PieChartData.Slice(
            "Unknown",
            stressData.filter { it.dummy }.size.toFloat(),
            MaterialTheme.colorScheme.onSurfaceVariant
        ),
        PieChartData.Slice(
            "Stressed",
            stressData.filter { it.stressLevel >= MonitorViewModel.STRESS_THRESHOLD }.size.toFloat(),
            MaterialTheme.colorScheme.tertiary
        ),
        PieChartData.Slice(
            "Not stressed",
            stressData.filter { it.stressLevel < MonitorViewModel.STRESS_THRESHOLD && !it.dummy }.size.toFloat(),
            MaterialTheme.colorScheme.onTertiary
        ),
    )

    // Define the data for the donut chart.
    val donutChartData = PieChartData(
        slices = slices,
        plotType = PlotType.Donut,
    )

    // Define the configuration for the donut chart.
    val donutChartConfig = PieChartConfig(
        strokeWidth = 40f,
        chartPadding = 20,
        activeSliceAlpha = .9f,
        isAnimationEnable = true,
        labelVisible = true,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    )

    // Define the layout of the donut chart.
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Define the legend of the donut chart.
        DonutChartLegend(slices = slices)

        // Define the box containing the donut chart.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            // Create the donut chart.
            DonutPieChart(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .width(150.dp),
                donutChartData,
                donutChartConfig
            )
        }
    }

}

/**
 * A Composable function that represents the legend of the donut chart.
 *
 * @param slices The slices to be displayed in the legend.
 */
@Composable
private fun DonutChartLegend(slices: List<PieChartData.Slice>) {

    // Define the layout of the legend.
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {

        // For each slice, create a row in the legend.
        slices.forEach { slice ->
            Row(
                modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                // Create a box representing the color of the slice.
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(slice.color)
                )

                // Create a spacer.
                Spacer(modifier = Modifier.width(8.dp))

                // Create a text representing the label of the slice.
                Text(
                    text = slice.label,
                    style = MaterialTheme.typography.bodyMedium,
                )

            }
        }
    }
}
