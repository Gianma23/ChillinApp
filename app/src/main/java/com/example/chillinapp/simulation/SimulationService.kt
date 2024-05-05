package com.example.chillinapp.simulation

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.chillinapp.data.AppDataContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


class SimulationService : Service() {

    private lateinit var simulationThread: Thread

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        simulationThread = Thread {
            simulateDataTransmission()
        }
        simulationThread.start()
        return START_STICKY
    }

    override fun onDestroy() {
        simulationThread.interrupt()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * Simulates the transmission of stress data by generating data for the last 30 seconds every 30 seconds
     * and sending it to the stress data service.
     */
    private fun simulateDataTransmission(){
        val appContainer = AppDataContainer()
        val stressDataService = appContainer.stressDataService

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {

                // Generate the data for the last 30 seconds, with a step of 1 second
                val data = generateStressRawDataList(
                    start = Calendar.getInstance().apply { add(Calendar.SECOND, -30) },
                    end = Calendar.getInstance(),
                    step = 1000,
                    invalidDataProbability = 0.1
                )
                Log.d("Simulation", "Generated data: $data")

                // Send the data to the stress data service
                CoroutineScope(Dispatchers.IO).launch {
                    val response = stressDataService.insertRawData(data)
                    Log.d("Simulation", "Sent data to service: ${response.success}")
                }

                // Schedule the next run in 30 seconds
                handler.postDelayed(this, 30000)
            }
        }

        // Start the runnable
        handler.post(runnable)
    }
}
