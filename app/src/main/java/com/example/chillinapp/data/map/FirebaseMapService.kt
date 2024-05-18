package com.example.chillinapp.data.map

import com.example.chillinapp.data.ServiceResult
import com.google.maps.android.heatmaps.WeightedLatLng
import java.time.LocalDate

class FirebaseMapService(private val mapDao: FirebaseMapDao): MapService {

    override suspend fun get(centerLat: Double, centerLong: Double, distance: Double, date: LocalDate, hour: Int): ServiceResult<List<WeightedLatLng>, MapErrorType> {
        return mapDao.get(centerLat, centerLong, distance, date, hour)
    }
}
