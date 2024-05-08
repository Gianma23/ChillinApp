package com.example.chillinapp.simulation

import android.util.Log
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.stress.StressErrorType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.heatmaps.WeightedLatLng
import java.util.Date
import java.util.Random


fun simulateStressDataService(center: LatLng, radius: Double, date: Date): ServiceResult<List<WeightedLatLng>, StressErrorType> {
    Log.d("StressDataService", "Generating stress data for $date")
    val response: ServiceResult<List<WeightedLatLng>, StressErrorType> =
        ServiceResult(
            success = true,
            data = generateStressDataList(center, radius),
            error = null
        )
    return response
}

private fun generateStressDataList(center: LatLng, radius: Double, numPoints: Int = 50): List<WeightedLatLng> {

    val stressDataList = mutableListOf<WeightedLatLng>()
    val random = Random()

    // Generate random stress data
    for (i in 0..numPoints) {
        val lat = center.latitude + (random.nextGaussian() * radius)
        val lng = center.longitude + (random.nextGaussian() * radius)
        val intensity = (0.5 + random.nextGaussian() * 0.5).coerceIn(0.0, 1.0)  // Mean 0.5, st.dev. 0.5
        stressDataList.add(WeightedLatLng(LatLng(lat, lng), intensity))
    }
    return stressDataList

}