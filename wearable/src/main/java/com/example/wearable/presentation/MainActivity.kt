/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.wearable.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.wearable.R
import com.example.wearable.presentation.theme.ChillinAppTheme
import com.example.wearable.synchronization.WearableDataProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }

        /*
        val intent = Intent(this, WearableDataProvider::class.java)
        intent.setAction("START_SERVICE")
        startService(intent)
            */

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, WearableDataProvider::class.java)
            intent.action = "START_SERVICE"
            startService(intent)
        }, 30000)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, WearableDataProvider::class.java)
            intent.action = "SEND"
            startService(intent)
        }, 10000)
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO: stop sensor services
        /*
        var intent = Intent(this, SensorHandler::class.java)
        intent.setAction("stop_sensors")
        startService(intent)
         */

        intent = Intent(this, WearableDataProvider::class.java)
        intent.setAction("STOP_SERVICE")
        startService(intent)
    }


}

@Composable
fun WearApp(greetingName: String) {
    ChillinAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}