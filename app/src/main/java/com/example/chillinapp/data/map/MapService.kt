package com.example.chillinapp.data.map

import com.example.chillinapp.data.ServiceResult


interface MapService {

    /**
     * Get all the coordinates and stress scores from the database
     * @return a list of maps
     */
    suspend fun get() : ServiceResult <List <Map>,MapErrorType>

    /**
     * Get all the coordinates and stress scores from the database within a certain distance from a center point
     * @param centerLat the latitude of the center point
     * @param centerLong the longitude of the center point
     * @param distance the distance from the center point
     * @return a list of maps
     */
    suspend fun get(centerLat: Double, centerLong: Double, distance: Double) : ServiceResult <List <Map>,MapErrorType>
}