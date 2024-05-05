package com.example.chillinapp.data

import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

private const val TAG = "LocationProvider"

object LocationProvider {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    var longitude: Double = 0.0
    var latitude: Double = 0.0

    fun setupLocationProvider(context : Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val lastLoc = p0.lastLocation
                if (lastLoc != null) {
                    longitude = lastLoc.longitude
                    latitude = lastLoc.latitude
                }
            }
        }
    }

    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(TimeUnit.SECONDS.toMillis(3))
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}