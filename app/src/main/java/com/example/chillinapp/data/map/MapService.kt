package com.example.chillinapp.data.map

import com.example.chillinapp.data.ServiceResult


interface MapService {

    /**
     * Get all the coordinates and stress scores from the database
     * @return a list of maps
     */
    suspend fun get() : ServiceResult <List <Map>,MapErrorType>

    /**
     * Get all the coordinates and stress scores from the database within the specified boundaries
     * @param minLat the minimum latitude
     * @param maxLat the maximum latitude
     * @param minLng the minimum longitude
     * @param maxLng the maximum longitude
     * @return a list of maps
     */
    suspend fun get(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double) : ServiceResult <List <Map>,MapErrorType>
}