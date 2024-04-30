package com.example.chillinapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import com.example.chillinapp.synchronization.WearableDataReceiver
import com.example.chillinapp.ui.theme.ChillInAppTheme
import java.lang.reflect.Modifier


/**
 * Main activity for the ChillIn application.
 *
 * This activity is the entry point for the application. It sets the content to the ChillInAppTheme, which
 * wraps the ChillInApp composable function. The ChillInApp function is responsible for setting up the
 * application's navigation and theme.
 */
class ChillInActivity : ComponentActivity() {

    /**
     * Called when the activity is starting.
     *
     * This method sets the content view to be the ChillInAppTheme, which wraps the ChillInApp composable function.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this
     * Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ChillInAppTheme {
                Box(modifier = Modifier.safeDrawingPadding()) {
                    ChillInApp()
                }
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