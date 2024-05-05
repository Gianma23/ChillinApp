package com.example.chillinapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chillinapp.synchronization.SensorDataHandler
import com.example.chillinapp.synchronization.WearableDataProvider
import com.google.android.gms.location.*
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

private const val TAG = "SensorService"

private const val SAMPLING_PERIOD: Int = 10000000 // 1sec

class SensorService: Service(), SensorEventListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var longitude = AtomicReference<Double>(0.0)
    private var latitude = AtomicReference<Double>(0.0)

    private var sensorManager: SensorManager? = null
    private var hrSensor: Sensor? = null
    private var edaSensor: Sensor? = null
    private var tempSensor: Sensor? = null
    private var lastHRValue: Float = 0f
    private var lastEDAValue: Float = 0f
    private var lastTempValue: Float = 0f

    override fun onCreate() {
        super.onCreate()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BODY_SENSORS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Permission denied")
            stopSelf()
            return
        }

        NotificationsHelper.createNotificationChannel(this)
        startForeground(
            1,
            NotificationsHelper.buildNotification(this),
        )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val lastLoc = p0.lastLocation
                if (lastLoc != null) {
                    latitude.set(lastLoc.latitude)
                    longitude.set(lastLoc.longitude)
                    Log.d(TAG, "location updated")
                }
            }
        }
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
        Log.d(TAG, "latitude: ${latitude.get()}, longitude: ${longitude.get()}")
        tmpByte = ByteBuffer.allocate(8).putDouble(latitude.get()).array()
        data = data.plus(tmpByte)
        tmpByte = ByteBuffer.allocate(8).putDouble(longitude.get()).array()
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


    @SuppressLint("MissingPermission")
    private fun startSensors() {
        if (hrSensor != null)
            sensorManager?.registerListener(this, hrSensor, SAMPLING_PERIOD)
        if (edaSensor != null)
            sensorManager?.registerListener(this, edaSensor, SAMPLING_PERIOD)
        if (tempSensor != null)
            sensorManager?.registerListener(this, tempSensor, SAMPLING_PERIOD)

        val locationRequest = LocationRequest.Builder(TimeUnit.SECONDS.toMillis(3))
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        Log.d(TAG, "Sensors started")
    }

    private fun stopSensors() {
        //dump dati sensori
        fusedLocationClient.removeLocationUpdates(locationCallback)
        sensorManager?.unregisterListener(this, hrSensor)
        Log.d(TAG, "Sensors stopped")
    }
}