package com.example.chillinapp.ui.access.utility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chillinapp.R


@Composable
fun SimpleNotification(
    action: () -> Unit = {},
    buttonText: String = stringResource(id = R.string.hide_notify_action),
    bodyText: String = "",
){
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(28.dp)
            .fillMaxSize()
    ) {
        Snackbar(
            action = {
                TextButton(
                    onClick = { action() },
                    modifier = Modifier
                ) {
                    Text(text = buttonText)
                }
            },
            modifier = Modifier
        ) {
            Text(text = bodyText)
        }
    }
}

@Preview()
@Composable
fun SimpleNotificationPreview() {
    SimpleNotification(
        action = {},
        buttonText = "Hide",
        bodyText = "This is a notification"
    )
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun SimpleNotificationPreviewLandscape() {
    SimpleNotification(
        action = {},
        buttonText = "Hide",
        bodyText = "This is a notification"
    )
}