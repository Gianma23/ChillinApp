package com.example.wearable

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.wearable.NotificationsHelper
import com.example.wearable.synchronization.SensorDataHandler
import com.example.wearable.synchronization.WearableDataProvider
import java.nio.ByteBuffer

private val TAG = "SensorService"

class SensorService: Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var hrSensor: Sensor? = null

    override fun onCreate() {
        super.onCreate()

        val sensorsPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
        if (sensorsPermission == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Permission denied")
            stopSelf()
            return
        }

        NotificationsHelper.createNotificationChannel(this)
        startForeground(
            1,
            NotificationsHelper.buildNotification(this),
        )
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        hrSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSensors()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action != null) {
            when(intent.action) {
                "start_sensors" -> startSensors()
                "stop_sensors" -> stopSensors()
                else -> {
                    TODO("throw error")
                }
            }
        }
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        var value: Float? = 0f
        var timestamp: Long? = 0
        if(event?.sensor == hrSensor) {
            value = event?.values?.get(0)
            timestamp = event?.timestamp
            Log.d(TAG, "Sensor value: $value")
        }
        val byteTimestamp = timestamp?.let { ByteBuffer.allocate(8).putLong(it).array() }
        val byteValue = value?.let { ByteBuffer.allocate(4).putFloat(it).array() }
        var data : ByteArray? = ByteArray(0)
        if (byteValue != null) {
            data = byteTimestamp?.plus(byteValue)
            data = data?.plus(ByteArray(12))
        }
        Log.d(TAG, "data length ${data?.size}")

        val dataHandler = SensorDataHandler.getInstance()
        val isFull = dataHandler.pushData(data)
        Log.d(TAG, "Data pushed to handler")

        if(isFull) {
            val intent = Intent(this, WearableDataProvider::class.java)
            intent.action = "SEND"
            startService(intent)
            Log.d(TAG, "Provider service called to send data")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // do nothing
    }

    // ============================= PRIVATE METHODS =============================

    private fun startSensors() {
        // TODO controllare che i sensori siano disponibili
        sensorManager?.registerListener(this, hrSensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.d(TAG, "Sensors started")
    }

    private fun stopSensors() {
        //dump dati sensori
        sensorManager?.unregisterListener(this, hrSensor)
        Log.d(TAG, "Sensors stopped")
    }
}