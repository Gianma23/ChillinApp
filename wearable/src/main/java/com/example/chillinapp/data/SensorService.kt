package com.example.chillinapp.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
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
import com.example.chillinapp.synchronization.SensorDataHandler
import com.example.chillinapp.synchronization.WearableDataProvider
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import uk.me.berndporr.iirj.Butterworth
import java.io.*
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

private const val TAG = "SensorService"

private const val SAMPLING_PERIOD: Int = 1000000 // 1sec

class SensorService: Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var hrSensor: Sensor? = null
    private var edaSensor: Sensor? = null
    private var tempSensor: Sensor? = null

    private var job: Job? = null
    private var lastHRValue: Float = 0f
    private var lastEDAValue: Float = 0f
    private var lastTempValue: Float = 0f
    private var highFilter = Butterworth()
    private var lowFilter = Butterworth()

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

        LocationProvider.setupLocationProvider(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        tempSensor = sensorManager?.getDefaultSensor(65555)
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
        if (event == null)
            return
        if (event.sensor != hrSensor && event.sensor != edaSensor && event.sensor != tempSensor) {
            return
        }
        val value =
        when(event.sensor) {
            hrSensor -> event.values[0]
            edaSensor -> event.values[2]
            tempSensor -> event.values[0]
            else -> 0f
        }
        //Log.d(TAG, "Sensor ${event.sensor?.name} new value: ${value}, time: ${System.currentTimeMillis()}")

        // DEBUG: save sensor data to files
        if (
            event.sensor == edaSensor
        ) {
            try {
                val timestamp = System.currentTimeMillis()
                val fileTitle =
                when(event.sensor) {
                    sensorManager?.getDefaultSensor(65554) -> "eda.csv"
                    else -> "test.csv"
                }
                val fos = openFileOutput(fileTitle, Context.MODE_APPEND)
                val writer = OutputStreamWriter(fos)

                var header = "timestamp"
                if (fos.channel.size() == 0L) {
                    for(i in 0..<event.values.size) {
                        header += ",value$i"
                    }
                    writer.write(header+"\n")
                }
                var dataTest = "$timestamp"
                for(i in 0..<event.values.size) {
                   dataTest = "$dataTest,${event.values[i]}"
                }
                writer.write(dataTest)
                writer.write("\n") // new line

                writer.close()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (value == 0f) {
            return
        }
        when (event.sensor) {
            hrSensor -> lastHRValue = value
            edaSensor -> lastEDAValue = value
            tempSensor -> lastTempValue = value
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // do nothing
    }

    // ============================= PRIVATE METHODS =============================

    private fun startSensors() {
        if (hrSensor != null) {
            sensorManager?.registerListener(this, hrSensor, SAMPLING_PERIOD)
            Log.d(TAG, "HR sensor started")
        }
        if (edaSensor != null) {
            sensorManager?.registerListener(this, edaSensor, SAMPLING_PERIOD)
            Log.d(TAG, "EDA sensor started")
        }
        if (tempSensor != null) {
            sensorManager?.registerListener(this, tempSensor, SAMPLING_PERIOD)
            Log.d(TAG, "Temp sensor started")
        }

        LocationProvider.startLocationUpdates()

        job?.cancel()
        job = MainScope().launch(Dispatchers.IO) {
            while(true) {
                saveData()
                delay(1000)
            }
        }

        highFilter.highPass(1, 1.0, 0.05)
        lowFilter.lowPass(1, 1.0, 0.49)

        Log.d(TAG, "Sensors started")
    }

    private fun stopSensors() {
        //TODO dump dati sensori
        job?.cancel()
        LocationProvider.stopLocationUpdates()
        sensorManager?.unregisterListener(this, hrSensor)
        sensorManager?.unregisterListener(this, edaSensor)
        sensorManager?.unregisterListener(this, tempSensor)
        Log.d(TAG, "Sensors stopped")
    }

    private fun saveData() {
        /*Log.d(TAG, "Save data\n" +
                "HR: $lastHRValue\n" +
                "EDA: $lastEDAValue\n" +
                "Temp: $lastTempValue\n" +
                "time: ${System.currentTimeMillis()}\n" +
                "location: ${LocationProvider.latitude}, ${LocationProvider.longitude}")*/

        var data = ByteArray(0)
        var tmpByte = ByteBuffer.allocate(8).putLong(System.currentTimeMillis()).array()
        data = data.plus(tmpByte)
        tmpByte = ByteBuffer.allocate(4).putFloat(lastEDAValue).array()
        data = data.plus(tmpByte)
        tmpByte = ByteBuffer.allocate(4).putFloat(lastTempValue).array()
        data = data.plus(tmpByte)
        tmpByte = ByteBuffer.allocate(4).putFloat(lastHRValue).array()
        data = data.plus(tmpByte)
        tmpByte = ByteBuffer.allocate(8).putDouble(LocationProvider.latitude).array()
        data = data.plus(tmpByte)
        tmpByte = ByteBuffer.allocate(8).putDouble(LocationProvider.longitude).array()
        data = data.plus(tmpByte)

        //val dataHandler = SensorDataHandler.instance
        val isFull = SensorDataHandler.pushData(data)

        if (isFull) {
            val intent = Intent(this, WearableDataProvider::class.java)
            intent.action = "SEND"
            startService(intent)
            Log.d(TAG, "Provider service called to send data")
        }

        // DEBUG: save sensor data to files
        /*try {
            val timestamp = System.currentTimeMillis()
            val header = "timestamp,eda"
            val fileTitle = "eda_filter.csv"
            lastEDAValue = lowFilter.filter(lastEDAValue.toDouble()).toFloat()
            writeOnFile(fileTitle, "$timestamp,${lastEDAValue}", header)

            val fileTitle2 = "eda_filter2.csv"
            lastEDAValue = highFilter.filter(lastEDAValue.toDouble()).toFloat()
            writeOnFile(fileTitle2, "$timestamp,${lastEDAValue}", header)
        } catch (e: IOException) {
            e.printStackTrace()
        }*/
    }

    private fun writeOnFile(fileTitle: String, data: String, header: String) {
        try {
            val fos = openFileOutput(fileTitle, Context.MODE_APPEND)
            val writer = OutputStreamWriter(fos)
            if (fos.channel.size() == 0L) {
                writer.write(header+"\n")
            }
            writer.write(data)
            writer.write("\n") // new line
            writer.close()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}