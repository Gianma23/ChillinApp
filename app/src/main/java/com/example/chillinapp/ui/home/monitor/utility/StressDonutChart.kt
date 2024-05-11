package com.example.chillinapp.ui.home.monitor.utility

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


@Composable
fun StressDonutChart(stressData: List<FormattedStressDerivedData>) {
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

    val donutChartData = PieChartData(
        slices = slices,
        plotType = PlotType.Donut,
    )

    val donutChartConfig = PieChartConfig(
        strokeWidth = 40f,
        chartPadding = 20,
        activeSliceAlpha = .9f,
        isAnimationEnable = true,
        labelVisible = true,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        DonutChartLegend(slices = slices)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
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

@Composable
private fun DonutChartLegend(slices: List<PieChartData.Slice>) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        slices.forEach { slice ->
            Row(
                modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(slice.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = slice.label,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
