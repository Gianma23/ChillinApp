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

/**
 * Service class for simulating the transmission of stress data.
 *
 * This service simulates the transmission of stress data by generating data for the last 30 seconds every 30 seconds
 * and sending it to the stress data service. The service runs in the background and is started with the START_STICKY flag,
 * meaning it will be restarted by the system if it gets killed.
 */
class SimulationService : Service() {

    /**
     * Thread used for the simulation of data transmission.
     */
    private lateinit var simulationThread: Thread

    /**
     * Called by the system every time a client explicitly starts the service by calling startService(Intent),
     * providing the arguments it supplied and a unique integer token representing the start request.
     *
     * @param intent The Intent supplied to startService(Intent), as given. This may be null if the service is being
     *               restarted after its process has gone away, and it had previously returned anything except START_STICKY_COMPATIBILITY.
     * @param flags Additional data about this start request. Currently either 0, START_FLAG_REDELIVERY, or START_FLAG_RETRY.
     * @param startId A unique integer representing this specific request to start. Use with stopSelfResult(int).
     * @return The return value indicates what semantics the system should use for the service's current started state.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        simulationThread = Thread {
            simulateDataTransmission()
        }
        simulationThread.start()
        return START_STICKY
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed. The service should clean up any resources
     * it holds (threads, registered receivers, etc) at this point. Upon return, there will be no more calls in to this Service object
     * and it is effectively dead.
     */
    override fun onDestroy() {
        simulationThread.interrupt()
        super.onDestroy()
    }

    /**
     * Return the communication channel to the service. May return null if clients can not bind to the service.
     * The returned IBinder is usually for a complex interface that has been described using aidl.
     *
     * @param intent The Intent that was used to bind to this service, as given to Context.bindService.
     *               Note that any extras that were included with the Intent at that point will not be seen here.
     * @return Return an IBinder through which clients can call on to the service.
     */
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * Simulates the transmission of stress data by generating data for the last 30 seconds every 30 seconds
     * and sending it to the stress data service.
     *
     * This method creates a new thread that generates stress data and sends it to the stress data service every 30 seconds.
     * The data is generated for the last 30 seconds, with a step of 1 second, and has a 10% probability of being invalid.
     * The generated data is logged and then sent to the stress data service using a coroutine.
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
