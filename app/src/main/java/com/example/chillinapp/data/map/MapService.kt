package com.example.chillinapp.data.map

import com.example.chillinapp.data.ServiceResult
import com.google.maps.android.heatmaps.WeightedLatLng
import java.time.LocalDate


interface MapService {

    /**
     * Get all the coordinates and stress scores from the database
     * @return a list of maps
     */
    // suspend fun get() : ServiceResult <List <Map<Any?, Any?>>,MapErrorType>

    /**
     * Get all the coordinates and stress scores from the database within a certain distance from a center point
     * @param centerLat the latitude of the center point
     * @param centerLong the longitude of the center point
     * @param distance the distance from the center point
     * @param date the date [default: today]
     * @param hour the hour [default: 0]
     * @return a list of maps
     */
    suspend fun get(centerLat: Double, centerLong: Double, distance: Double, date: LocalDate, hour: Int) : ServiceResult <List <WeightedLatLng>,MapErrorType>
}