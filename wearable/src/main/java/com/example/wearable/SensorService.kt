package com.example.wearable

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder

private val TAG = "SensorService"

class SensorService: Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var EDAsensor: Sensor? = null
    private var tempSensor: Sensor? = null

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        EDAsensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE) // TODO change to EDA sensor
        tempSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
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
        TODO("Not yet implemented")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }

    // ============================= PRIVATE METHODS =============================

    private fun startSensors() {
        sensorManager?.registerListener(this, EDAsensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager?.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun stopSensors() {
        sensorManager?.unregisterListener(this)
    }
}