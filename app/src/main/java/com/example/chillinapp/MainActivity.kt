package com.example.chillinapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chillinapp.synchronization.WearableDataReceiver
import com.example.chillinapp.ui.theme.ChillinAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChillinAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChillinAppTheme {
        Greeting("Android")
    }
}