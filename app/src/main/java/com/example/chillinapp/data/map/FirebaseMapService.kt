package com.example.chillinapp.data.map

import com.example.chillinapp.data.ServiceResult

class FirebaseMapService(private val mapDao: FirebaseMapDao): MapService {

    override suspend fun get(): ServiceResult<List<Map>, MapErrorType> {
        return mapDao.get()
    }

    override suspend fun get(centerLat: Double, centerLong: Double, distance: Double): ServiceResult<List<Map>, MapErrorType> {
        return mapDao.get(centerLat, centerLong, distance)
    }
}