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
    private const val SAMPLING_PERIOD_SEC: Long = 30
    private const val DECIMAL_PLACES = 3
    var longitude: Double = 0.0
    var latitude: Double = 0.0

    fun setupLocationProvider(context : Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val lastLoc = p0.lastLocation
                if (lastLoc != null) {
                    longitude = BigDecimal(lastLoc.longitude).setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN).toDouble()
                    latitude = BigDecimal(lastLoc.latitude).setScale(DECIMAL_PLACES, RoundingMode.HALF_EVEN).toDouble()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(TimeUnit.SECONDS.toMillis(SAMPLING_PERIOD_SEC))
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}