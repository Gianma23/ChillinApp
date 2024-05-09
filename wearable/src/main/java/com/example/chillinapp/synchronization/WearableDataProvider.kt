package com.example.chillinapp.synchronization

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.ExecutionException

/**
 * This class is responsible for sending data to the handheld device.
 */
class WearableDataProvider : Service() {
    private val tag = "WearableDataProvider"
    private val channelMsg = "/chillinapp"
    override fun onBind(intent: Intent): IBinder? {
        Log.d(tag, "onBind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "Service created")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // Check the action received
        if (intent.action != null && intent.action == "SEND") {
            Log.d(tag, "Sending data")
            //val sensorDataHandler = SensorDataHandler.
            val data = SensorDataHandler.getData()
            sendData(data)
        } else if (intent.action != null && intent.action == "STOP_SERVICE") {
            Log.d(tag, "Stopping service")
            stopSelf()
        } else if (intent.action != null && intent.action == "START_SERVICE") {
            Log.d(tag, "Starting service")
        } else Log.w(tag, "No action found")

        // Return START_STICKY to restart the service if it gets killed
        return START_STICKY
    }

    private fun sendData(data: ByteArray) {
        val toRun = Runnable {
            val nodeId = node
            Log.d(tag, "Node: $nodeId")

            // Open a channel to send data
            val channelTask =
                Wearable.getChannelClient(applicationContext).openChannel(
                    nodeId!!, channelMsg
                )
            channelTask.addOnSuccessListener { channel: ChannelClient.Channel ->
                Log.d(tag, "onSuccess " + channel.nodeId)
                Log.d(tag, "Channel: " + channel.path)
                // Get the output stream
                val outputStreamTask =
                    Wearable.getChannelClient(applicationContext).getOutputStream(channel)
                // print details of the output stream task
                Log.d(tag, "output stream task: $outputStreamTask")
                outputStreamTask.addOnSuccessListener { outputStream: OutputStream ->
                    Log.d(tag, "output stream onSuccess")
                    try {
                        outputStream.write(data)
                        outputStream.flush()
                        outputStream.close()
                    } catch (e: IOException) {
                        Log.e(tag, "Error in sending data: $e")
                    }
                    Log.d(tag, "Data sent")
                }
            }
        }
        val run = Thread(toRun)
        run.start()
    }

    private val node: String?
        /**
         * Get the node ID of the handheld device.
         * @return The node ID of the handheld device.
         */
        get() {
            var nodeId: String? = null
            val nodeListTask = Wearable.getNodeClient(
                applicationContext
            ).connectedNodes
            try {
                val nodes = Tasks.await(nodeListTask)
                check(nodes.size == 1) { "Unexpected number of nodes found: " + nodes.size }
                nodeId = nodes[0].id
            } catch (exception: ExecutionException) {
                Log.e(tag, "Task failed: $exception")
            } catch (exception: InterruptedException) {
                Log.e(tag, "Interrupt occurred: $exception")
            }
            return nodeId
        }
}


