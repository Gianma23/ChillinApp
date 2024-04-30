package com.example.chillinapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.chillinapp.synchronization.WearableDataReceiver
import com.example.chillinapp.ui.theme.ChillInAppTheme

class ChillInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChillInAppTheme {
                ChillInApp()
            }
        }
        val intent = Intent(this, WearableDataReceiver::class.java)
        intent.setAction("START_SERVICE")
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, WearableDataReceiver::class.java)
        intent.setAction("STOP_SERVICE")
        startService(intent)
    }
}