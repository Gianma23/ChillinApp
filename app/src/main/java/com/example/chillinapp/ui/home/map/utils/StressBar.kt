package com.example.chillinapp.ui.home.map.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chillinapp.ui.theme.ChillInAppTheme
import com.google.maps.android.heatmaps.Gradient


@Composable
internal fun StressBar(
    gradient: Gradient,
    minValue: Int?,
    maxValue: Int?,
    modifier: Modifier = Modifier
) {

    val colors = gradient.mColors.map { Color(it).copy(alpha = 0.7f) }.reversed()

    val brush = Brush.verticalGradient(
        colors = colors
    )

    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = maxValue?.toString() ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(4.dp)
            )
        }

        Box(
            modifier = Modifier
                .background(brush)
                .height(400.dp)
                .padding(4.dp),
        )

        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = minValue?.toString() ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(4.dp)
            )
        }
    }
}

@Preview
@Composable
fun StressBarPreview() {
    val gradient = Gradient(
        intArrayOf(
            android.graphics.Color.rgb(102, 225, 0),  // green
            android.graphics.Color.rgb(255, 0, 0) // red
        ),
        floatArrayOf(0.2f, 1f)
    )

    ChillInAppTheme {
        StressBar(
            gradient = gradient,
            minValue = 0,
            maxValue = 100,
            modifier = Modifier
        )
    }
}
