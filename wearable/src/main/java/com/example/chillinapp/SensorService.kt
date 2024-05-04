package com.example.chillinapp

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.chillinapp.synchronization.SensorDataHandler
import com.example.chillinapp.synchronization.WearableDataProvider
import java.nio.ByteBuffer

private const val TAG = "SensorService"

private const val SAMPLING_PERIOD: Int = 10000000 // 1sec

class SensorService: Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var hrSensor: Sensor? = null
    private var edaSensor: Sensor? = null
    private var tempSensor: Sensor? = null
    private var lastHRValue: Float = 0f
    private var lastEDAValue: Float = 0f
    private var lastTempValue: Float = 0f

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
        tempSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        hrSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        edaSensor = sensorManager?.getDefaultSensor(65554)
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
        if (event?.sensor != hrSensor && event?.sensor != edaSensor && event?.sensor != tempSensor) {
            return
        }
        val value = event?.values?.get(0)
        val timestamp = event?.timestamp
        Log.d(TAG, "Sensor ${event?.sensor?.name} new value: $value")
        
        if (value == null || timestamp == null || value == 0f) {
            return
        }
        when (event.sensor) {
            hrSensor -> lastHRValue = value
            edaSensor -> lastEDAValue = value
            tempSensor -> lastTempValue = value
        }
        
        var data = ByteArray(0)
        var tmpByte = ByteBuffer.allocate(8).putLong(timestamp).array()
        data = data.plus(tmpByte)
        tmpByte = ByteBuffer.allocate(4).putFloat(lastEDAValue).array()
        data = data.plus(tmpByte)
        tmpByte = ByteBuffer.allocate(4).putFloat(lastTempValue).array()
        data = data.plus(tmpByte)
        tmpByte = ByteBuffer.allocate(4).putFloat(lastHRValue).array()
        data = data.plus(tmpByte)

        val dataHandler = SensorDataHandler.getInstance()
        val isFull = dataHandler.pushData(data)

        if (isFull) {
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
        sensorManager?.registerListener(this, hrSensor, SAMPLING_PERIOD)
        sensorManager?.registerListener(this, edaSensor, SAMPLING_PERIOD)
        sensorManager?.registerListener(this, tempSensor, SAMPLING_PERIOD)
        Log.d(TAG, "Sensors started")
    }

    private fun stopSensors() {
        //dump dati sensori
        sensorManager?.unregisterListener(this, hrSensor)
        Log.d(TAG, "Sensors stopped")
    }
}