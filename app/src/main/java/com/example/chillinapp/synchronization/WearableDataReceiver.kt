package com.example.chillinapp.synchronization

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.chillinapp.data.stress.FirebaseStressDataDao
import com.example.chillinapp.data.stress.FirebaseStressDataService
import com.example.chillinapp.data.stress.StressRawData
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.coroutines.CoroutineContext

class WearableDataReceiver : Service(), CoroutineScope {
    private val tag = "WearableDataReceiver"
    private val channelMsg = "/chillinapp"
    private lateinit var job: Job
    private val bytesPerSample = 36


    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate(){
        super.onCreate()
        job = Job()
        Log.d(tag, "Service created")
    }

    override fun onDestroy(){
        super.onDestroy()
        job.cancel()
        Log.d(tag, "Service destroyed")
    }

    override fun onStartCommand(intent: Intent?,flags: Int,startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // Check the action received
        when (intent?.action) {
            "START_SERVICE" -> {
                Log.d(tag, "Starting service")
                receiveData()

                // Get the node id of itself
                Wearable.getNodeClient(applicationContext).localNode.addOnSuccessListener { node ->
                    val nodeId = node.id
                    Log.d(tag, "Node id: $nodeId")
                }
            }
            "STOP_SERVICE" -> {
                Log.d(tag, "Stopping service")
                stopSelf()
            }
            else -> Log.w(tag, "No action found")
        }

        // Return START_STICKY to restart the service if it gets killed
        return START_STICKY
    }

    private fun receiveData() {
        Log.d(tag, "Receiving data")
        Log.d(tag, "Channel client: ${Wearable.getChannelClient(applicationContext)}")
        // Register a channel callback to receive data from the wearable device
        Wearable.getChannelClient(applicationContext).registerChannelCallback(object : ChannelClient.ChannelCallback() {

            override fun onChannelOpened(channel: ChannelClient.Channel) {
                super.onChannelOpened(channel)
                if (channel.path != channelMsg) {
                    Log.e(tag, "Channel not found")
                    return
                }
                Log.d(tag, "onChannelOpened")
                Log.d(tag, "Channel: ${channel.path}")
                val inputStreamTask: Task<InputStream> = Wearable.getChannelClient(applicationContext).getInputStream(channel)
                inputStreamTask.addOnSuccessListener{ inputStream ->
                    launch {
                        try {
                            Log.d(tag, "input stream found")
                            val buffer = ByteArrayOutputStream()
                            var read: Int
                            val data = ByteArray(1024)
                            while (inputStream.read(data, 0, data.size).also { read = it } != -1) {
                                buffer.write(data, 0, read)
                                buffer.flush()
                            }

                            val stressRawDataList = parseBulkData(buffer.toByteArray())
                            Log.d(tag, "Data received: ${stressRawDataList.size}")
                            val firebaseStressDataService = FirebaseStressDataService(FirebaseStressDataDao())
                            firebaseStressDataService.insertRawData(stressRawDataList)
                            inputStream.close()
                        } catch (e: IOException) {
                            Log.e(tag, "Error in receiving data: $e")
                        } finally {
                            Wearable.getChannelClient(applicationContext).close(channel)
                        }
                    }
                }
            }
        })
    }

    /**
     * Parse the data received from the wearable device. The data is composed by a list of 30 StressRawData objects.
     * @param data The data received from the wearable device.
     * @return A list of StressRawData objects.
     */
    private fun parseBulkData(data: ByteArray): List<StressRawData> {
        val sensorDataList = ArrayList<StressRawData>()
        var i = 0
        while (i < data.size) {
            val singleData = ByteArray(bytesPerSample)
            System.arraycopy(data, i, singleData, 0, bytesPerSample)
            val sensorData = parseSingleData(singleData)
            sensorDataList.add(sensorData)
            i += bytesPerSample
        }
        return sensorDataList
    }

    private fun parseSingleData(data: ByteArray): StressRawData {

        // Timestamp
        val timestampBytes = ByteArray(8)
        System.arraycopy(data, 0, timestampBytes, 0, 8)
        val timestamp = bytesToLong(timestampBytes)

        // EDA
        val edaBytes = ByteArray(4)
        System.arraycopy(data, 8, edaBytes, 0, 4)
        val eda = bytesToFloat(edaBytes)

        // Temperature
        val temperatureBytes = ByteArray(4)
        System.arraycopy(data, 12, temperatureBytes, 0, 4)
        val temperature = bytesToFloat(temperatureBytes)

        // heart rate
        val heartRateBytes = ByteArray(4)
        System.arraycopy(data, 16, heartRateBytes, 0, 4)
        val hr = bytesToFloat(heartRateBytes)

        // latitude
        val latitudeBytes = ByteArray(8)
        System.arraycopy(data, 20, latitudeBytes, 0, 8)
        val latitude = bytesToDouble(latitudeBytes)

        // longitude
        val longitudeBytes = ByteArray(8)
        System.arraycopy(data, 28, longitudeBytes, 0, 8)
        val longitude = bytesToDouble(longitudeBytes)

        Log.d(tag, "Latitude: $latitude, Longitude: $longitude")
        return StressRawData(timestamp, eda, temperature, hr, latitude, longitude)
    }

    /**
     * Converts a byte array to a long
     * @param timestampBytes
     * @return
     */
    private fun bytesToLong(timestampBytes: ByteArray): Long {
        val buffer = ByteBuffer.allocate(java.lang.Long.BYTES)
        buffer.put(timestampBytes)
        buffer.flip()
        return buffer.long
    }

    /**
     * Converts a byte array to a double
     * @param sensorBytes
     * @return
     */
    private fun bytesToDouble(sensorBytes: ByteArray): Double {
        val buffer = ByteBuffer.allocate(java.lang.Double.BYTES)
        buffer.put(sensorBytes)
        buffer.flip()
        return buffer.double
    }

    private fun bytesToFloat(sensorBytes: ByteArray): Float {
        val buffer = ByteBuffer.allocate(java.lang.Float.BYTES)
        buffer.put(sensorBytes)
        buffer.flip()
        return buffer.float
    }
}
