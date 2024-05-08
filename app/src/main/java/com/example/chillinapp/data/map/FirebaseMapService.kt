package com.example.chillinapp.data.map

import com.example.chillinapp.data.ServiceResult
import java.time.LocalDate

class FirebaseMapService(private val mapDao: FirebaseMapDao): MapService {

    /*
    override suspend fun get(): ServiceResult<List<Map<Any?, Any?>>, MapErrorType> {
        return mapDao.get()
    }
    */

    override suspend fun get(centerLat: Double, centerLong: Double, distance: Double, date: LocalDate, hour: Int): ServiceResult<List<Coordinate>, MapErrorType> {
        return mapDao.get(centerLat, centerLong, distance)
    }
}
