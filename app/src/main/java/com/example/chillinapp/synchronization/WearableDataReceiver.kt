package com.example.chillinapp.synchronization;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.util.Log;

import com.example.chillinapp.data.stress.FirebaseStressDataDao;
import com.example.chillinapp.data.stress.FirebaseStressDataService;
import com.example.chillinapp.data.stress.StressRawData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.*
import kotlinx.coroutines.*

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import kotlin.coroutines.CoroutineContext

class WearableDataReceiver : WearableListenerService(), CoroutineScope {
    private val TAG = "WearableDataReceiver"
    private val CHANNEL_MSG = "/chillinapp"
    private lateinit var job: Job
    private val BYTES_PER_SAMPLE = 20

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

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
            "START_SERVICE" -> {
                Log.d(TAG, "Starting service")
            }
            "STOP_SERVICE" -> {
                Log.d(TAG, "Stopping service")
                stopSelf()
            }
            else -> Log.w(TAG, "No action found")
        }

        // Return START_STICKY to restart the service if it gets killed
        return START_STICKY
    }

    override fun onConnectedNodes(p0: MutableList<Node>) {
        super.onConnectedNodes(p0)
        Log.d(TAG, "Connected nodes: $p0")
    }

    override fun onDataChanged(p0: DataEventBuffer) {
        super.onDataChanged(p0)
        Log.d(TAG, "Data changed: $p0")
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        Log.d(TAG, "Message received: $messageEvent")
    }

    override fun onChannelClosed(channel: ChannelClient.Channel, p1: Int, p2: Int) {
        super.onChannelClosed(channel, p1, p2)
        Log.d(TAG, "Channel closed")
    }

    override fun onOutputClosed(p0: Channel, p1: Int, p2: Int) {
        super.onOutputClosed(p0, p1, p2)
        Log.d(TAG, "Output closed")
    }

    override fun onChannelOpened(channel: ChannelClient.Channel) {
        Log.d(TAG, "onChannelOpened")
        super.onChannelOpened(channel);
        if (channel.path != CHANNEL_MSG) {
            Log.e(TAG, "Channel not found")
            return
        }
        Log.e(TAG, "onChannelOpened");
        Log.d(TAG, "Channel: ${channel.path}")
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
                        Log.e(TAG, "Data length $read")
                        buffer.write(data, 0, read)
                        buffer.flush()
                        val byteArray = buffer.toByteArray()
                        text.append(String(byteArray, StandardCharsets.UTF_8))
                    }
                    Log.e(TAG, "Reading: $text")

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

    private fun receiveData() {
        Log.d(TAG, "Receiving data")
        Log.d(TAG, "Channel client: ${Wearable.getChannelClient(applicationContext)}")
        // Register a channel callback to receive data from the wearable device
        /*Wearable.getChannelClient(applicationContext).registerChannelCallback(object : ChannelClient.ChannelCallback() {

            override fun onChannelOpened(channel: ChannelClient.Channel) {
                super.onChannelOpened(channel);
                if (channel.path != CHANNEL_MSG) {
                    Log.e(TAG, "Channel not found")
                    return
                }
                Log.d(TAG, "onChannelOpened");
                Log.d(TAG, "Channel: ${channel.path}")
                val inputStreamTask: Task<InputStream> = Wearable.getChannelClient(applicationContext).getInputStream(channel)
                inputStreamTask.addOnSuccessListener{ inputStream ->
                    launch {
                        try {
                            val buffer = ByteArrayOutputStream()
                            var read: Int
                            val data = ByteArray(1024)
                            while (inputStream.read(data, 0, data.size).also { read = it } != -1) {
                                buffer.write(data, 0, read)
                                buffer.flush()
                            }

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
        })*/
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
            val singleData = ByteArray(BYTES_PER_SAMPLE)
            System.arraycopy(data, i, singleData, 0, BYTES_PER_SAMPLE)
            val sensorData = parseSingleData(singleData)
            sensorDataList.add(sensorData)
            i += BYTES_PER_SAMPLE
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
        val heartRateBytes = ByteArray(4)
        System.arraycopy(data, 8, heartRateBytes, 0, 4)
        val hr = bytesToFloat(heartRateBytes)

        // GPS
        val gpsBytes = ByteArray(8)
        System.arraycopy(data, 12, gpsBytes, 0, 8)
        val skinTemperature = bytesToDouble(gpsBytes)

        return StressRawData(timestamp, hr, skinTemperature)
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
