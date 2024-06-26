package com.example.chillinapp.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.wear.compose.material.*
import com.example.chillinapp.R
import com.example.chillinapp.data.SensorService
import com.example.chillinapp.presentation.theme.ChillinAppTheme

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {

    private var permissions = arrayOf(
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp(modifier = Modifier.fillMaxSize())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }
        //TODO: request permission for background locaiton

        ActivityCompat.requestPermissions(this@MainActivity, permissions, 50)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        stopService(Intent(this, SensorService::class.java))
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission)
            == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        }
    }
}

@Composable
fun WearApp(modifier: Modifier = Modifier) {
    ChillinAppTheme {
        Scaffold (
            modifier = modifier
                .background(MaterialTheme.colors.background),
            timeText = { TimeText() },
            content = { SensorSwitch(modifier) }
        )
    }
}

@Composable
fun SensorSwitch(modifier: Modifier = Modifier) {
    var isChecked by rememberSaveable { mutableStateOf(false) }
    toggleSensorService(LocalContext.current, isChecked)

    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            lineHeight = 24.sp,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.secondary,
            text =
            if (isChecked)
                stringResource(R.string.sensor_enabled)
            else
                stringResource(R.string.sensor_disabled),
        )
        Spacer(modifier = Modifier.height(28.dp))
        Switch(
            modifier = Modifier.scale(4f),
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                Log.d("SensorSwitch", "Switch is checked: $it")
            }
        )
    }
}

fun toggleSensorService(context: Context, isChecked: Boolean) {
    val intent = Intent(context, SensorService::class.java)

    if (isChecked) {
        intent.setAction("start_sensors")
        startForegroundService(context, intent)
    } else {
        context.stopService(intent)
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(modifier = Modifier.fillMaxSize())
}