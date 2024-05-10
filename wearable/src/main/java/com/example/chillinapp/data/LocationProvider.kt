package com.example.chillinapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

private const val TAG = "LocationProvider"

object LocationProvider {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private const val SAMPLING_PERIOD_SEC: Long = 60
    private const val DECIMAL_PLACES = 3
    private const val LENGTH_SQUARE = 100 // meters
    var longitude: Double = 0.0
    var latitude: Double = 0.0

    fun setupLocationProvider(context : Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val lastLoc = p0.lastLocation ?: return
                longitude = BigDecimal(lastLoc.longitude).setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN).toDouble()
                latitude = BigDecimal(lastLoc.latitude).setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN).toDouble()

                // calculate the time (in seconds) to do 100 meters at the current speed
                val timeToCross = LENGTH_SQUARE / lastLoc.speed
                if(timeToCross < SAMPLING_PERIOD_SEC - 5 || timeToCross > SAMPLING_PERIOD_SEC + 5) {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    startLocationUpdates(timeToCross.toLong())
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(samplingPeriod: Long = SAMPLING_PERIOD_SEC) {
        val locationRequest = LocationRequest.Builder(TimeUnit.SECONDS.toMillis(samplingPeriod))
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}