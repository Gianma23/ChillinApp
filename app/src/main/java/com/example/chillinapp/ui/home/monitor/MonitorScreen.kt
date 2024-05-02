package com.example.chillinapp.ui.home.monitor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.navigation.NavigationDestination

object MonitorDestination: NavigationDestination {
    override val route: String = "overall"
    override val titleRes: Int = R.string.overview_title
}

@Composable
fun MonitorScreen(
    modifier: Modifier = Modifier,
    viewModel: MonitorViewModel = viewModel()
) {

    // Collect the UI state from the view model
    val uiState by viewModel.uiState.collectAsState()

    // Create a map where the key is the field name and the value is a list of pairs (timestamp, field value)
    val fieldValuesMap = mutableMapOf<String, MutableList<Pair<String, Any>>>()
    uiState.stressData.forEach { data ->
        FormattedStressRawData::class.java.declaredFields
            .filter { it.name != "timestamp" && it.name != "\$stable" }
            .forEach { field ->
                field.isAccessible = true
                field.get(data)?.let { value ->
                    fieldValuesMap.getOrPut(field.name) { mutableListOf() }.add(Pair(data.timestamp, value))
                }
            }
    }

    val titleFormatMap = mapOf(
        "heartRateSensor" to "Heart Rate",
        "skinTemperatureSensor" to "Skin Temperature"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(fieldValuesMap.entries.toList()) { entry ->
            Card(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val titleFormatFunction = titleFormatMap[entry.key]
                    Text(
                        text = titleFormatFunction ?: entry.key,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    entry.value.forEach { pair ->
                        Text(text = "Timestamp: ${pair.first}, Value: ${pair.second}")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonitorScreenPreview() {
    MonitorScreen()
}
