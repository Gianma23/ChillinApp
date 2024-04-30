package com.example.chillinapp.synchronization;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import android.util.Log;

import com.example.chillinapp.data.stress.FirebaseStressDataDao;
import com.example.chillinapp.data.stress.FirebaseStressDataService;
import com.example.chillinapp.data.stress.StressRawData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.ChannelClient;
import com.google.android.gms.wearable.Wearable;
import kotlinx.coroutines.*

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import kotlin.coroutines.CoroutineContext

class WearableDataReceiver : Service(), CoroutineScope {
    private val TAG = "WearableDataReceiver"
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate(){
        super.onCreate();
        job = Job()
        Log.d(TAG, "Service created")
    }

    override fun onDestroy(){
        super.onDestroy();
        job.cancel()
        Log.d(TAG, "Service destroyed")
    }

    override fun onStartCommand(intent: Intent?,flags: Int,startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // Check the action received
        when (intent?.action) {
            "RECEIVE" -> {
                Log.d(TAG, "Receiving data")
                receiveData()
            }
            "STOP_SERVICE" -> {
                Log.d(TAG, "Stopping service")
                stopSelf()
            }
            "START_SERVICE" -> Log.d(TAG, "Starting service")
            else -> Log.w(TAG, "No action found")
        }

        // Return START_STICKY to restart the service if it gets killed
        return START_STICKY
    }

    private fun receiveData() {
        // Register a channel callback to receive data from the wearable device
        Wearable.getChannelClient(applicationContext).registerChannelCallback(object : ChannelClient.ChannelCallback() {

            override fun onChannelOpened(channel: ChannelClient.Channel) {
                super.onChannelOpened(channel);
                Log.d(TAG, "onChannelOpened");
                val inputStreamTask: Task<InputStream> = Wearable.getChannelClient(applicationContext).getInputStream(channel)
                inputStreamTask.addOnSuccessListener{ inputStream ->
                    launch {
                        try {
                            // TODO: remove useless logs after testing
                            val text = StringBuilder()
                            val buffer = ByteArrayOutputStream()
                            var read: Int
                            val data = ByteArray(1024)
                            while (inputStream.read(data, 0, data.size).also { read = it } != -1) {
                                Log.d(TAG, "Data length $read")
                                buffer.write(data, 0, read)
                                buffer.flush()
                                val byteArray = buffer.toByteArray()
                                text.append(String(byteArray, StandardCharsets.UTF_8))
                            }
                            Log.d(TAG, "Reading: $text")

                            val stressRawDataList = parseBulkData(buffer.toByteArray())
                            val firebaseStressDataService = FirebaseStressDataService(FirebaseStressDataDao())
                            firebaseStressDataService.insertRawData(stressRawDataList)
                            inputStream.close()
                        } catch (e: IOException) {
                            Log.e(TAG, "Error in receiving data: $e")
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
            val singleData = ByteArray(24)
            System.arraycopy(data, i, singleData, 0, 24)
            val sensorData = parseSingleData(singleData)
            sensorDataList.add(sensorData)
            i += 24
        }
        return sensorDataList
    }

    private fun parseSingleData(data: ByteArray): StressRawData {
        // Array of bytes composed by:
        // 8 bytes for timestamp
        // 8 bytes for heart rate
        // 8 bytes for gps

        // Timestamp
        val timestampBytes = ByteArray(8)
        System.arraycopy(data, 0, timestampBytes, 0, 8)
        val timestamp = bytesToLong(timestampBytes)

        // heart rate
        val heartRateBytes = ByteArray(8)
        System.arraycopy(data, 8, heartRateBytes, 0, 8)
        val eda = bytesToDouble(heartRateBytes)

        // GPS
        val gpsBytes = ByteArray(8)
        System.arraycopy(data, 16, gpsBytes, 0, 8)
        val skinTemperature = bytesToDouble(gpsBytes)

        return StressRawData(timestamp, eda, skinTemperature)
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
}
