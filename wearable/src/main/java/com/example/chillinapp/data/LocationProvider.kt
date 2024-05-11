package com.example.chillinapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit


private const val TAG = "LocationProvider"

object LocationProvider {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private const val SLOW_SAMPLING_PERIOD: Long = 60
    private const val FAST_SAMPLING_PERIOD: Long = 5
    private var samplingPeriod: Long = SLOW_SAMPLING_PERIOD
    private const val DECIMAL_PLACES = 3
    private const val LENGTH_SQUARE = 100 // meters
    var longitude: Double = 0.0
    var latitude: Double = 0.0

    @SuppressLint("MissingPermission")
    fun setupLocationProvider(context : Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    longitude = BigDecimal(location.longitude).setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN).toDouble()
                    latitude = BigDecimal(location.latitude).setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN).toDouble()
                }
            }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val lastLoc = p0.lastLocation ?: return
                longitude = BigDecimal(lastLoc.longitude).setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN).toDouble()
                latitude = BigDecimal(lastLoc.latitude).setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN).toDouble()

                // calculate the time (in seconds) to do 100 meters at the current speed
                val speed = lastLoc.speed
                if (speed == 0f) {
                    return
                }
                val timeToCross = LENGTH_SQUARE / speed

                if(timeToCross < samplingPeriod - 5 || timeToCross > samplingPeriod + 5) {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    samplingPeriod = when {
                        timeToCross > SLOW_SAMPLING_PERIOD -> SLOW_SAMPLING_PERIOD
                        timeToCross < FAST_SAMPLING_PERIOD -> FAST_SAMPLING_PERIOD
                        else -> timeToCross.toLong()
                    }
                    startLocationUpdates(samplingPeriod)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(samplingPeriod: Long = SLOW_SAMPLING_PERIOD) {
        val locationRequest = LocationRequest.Builder(TimeUnit.SECONDS.toMillis(samplingPeriod))
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        Log.d(TAG, "Location updates started with sampling period: $samplingPeriod")
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}