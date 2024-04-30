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

private val TAG = "SensorService"

class SensorService: Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var EDAsensor: Sensor? = null
    private var tempSensor: Sensor? = null
    private var nSample = 0
    override fun onCreate() {
        super.onCreate()

        val sensorsPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
        if (sensorsPermission == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "Permission denied")
            stopSelf()
            return
        }

        val manager = NotificationsHelper.createNotificationChannel(this)
        startForeground(
            1,
            NotificationsHelper.buildNotification(this),
        )
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        EDAsensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE) // TODO change to EDA sensor
        tempSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
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
        Log.d(TAG, sensorManager?.getSensorList(Sensor.TYPE_ALL).toString())
        // startForeground()
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val value: Float?
        if(event?.sensor == EDAsensor) {
            value = event?.values?.get(0)
            Log.d(TAG, "EDA value: $value")
        }
        else if(event?.sensor == tempSensor) {
            value = event?.values?.get(0)
            Log.d(TAG, "Temperature value: $value")
        }
        // inserisci in coda
        nSample++
        // se coda piena, invia dati
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // do nothing
    }

    // ============================= PRIVATE METHODS =============================

    private fun startSensors() {
        nSample = 0
        sensorManager?.registerListener(this, EDAsensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager?.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.d(TAG, "Sensors started")
    }

    private fun stopSensors() {
        //dump dati sensori
        sensorManager?.unregisterListener(this, EDAsensor)
        sensorManager?.unregisterListener(this, tempSensor)
        Log.d(TAG, "Sensors stopped")
    }
}