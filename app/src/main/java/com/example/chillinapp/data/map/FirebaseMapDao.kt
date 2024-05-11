package com.example.chillinapp.data.map

import android.util.Log
import com.example.chillinapp.data.ServiceResult
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FirebaseMapDao {

    private val db: FirebaseFirestore = Firebase.firestore
    private val mapCollection = db.collection("Map")

    /**
     * Get all the documents in the Map collection
     * @return a ServiceResult object containing the list of Map objects if the operation was successful, an physiologicalError type otherwise
     */
    /*
    suspend fun get(): ServiceResult<List<Map>, MapErrorType> {
        return try {
            val mapList = mutableListOf<Map>()

            // Effettua una query per ottenere tutti i documenti della collezione Map
            val querySnapshot = mapCollection.get().await()

            // Itera sui documenti restituiti e converte i dati in oggetti Map
            querySnapshot.forEach { document ->
                val latitude = document.get("latitude") as Double
                val longitude = document.get("longitude") as Double
                val stressScore = document.get("stressScore") as Float

                // Costruisci l'oggetto Map e aggiungilo alla lista
                val map = Map(latitude, longitude, stressScore)
                mapList.add(map)
            }

            ServiceResult(true, mapList, null)
        } catch (e: Exception) {
            ServiceResult(false, null, MapErrorType.NETWORK_ERROR)
        }
    }*/

    /**
     * Get all the documents within the specified distance from the center
     * @param centerLat the latitude of the center
     * @param centerLng the longitude of the center
     * @param distance the distance from the center
     * @param date the date [default: today]
     * @param hour the hour
     * @return a ServiceResult object containing the list of Map objects if the operation was successful, an physiologicalError type otherwise
     */
    suspend fun get(centerLat: Double,
                    centerLng: Double,
                    distance: Double,
                    date: LocalDate,
                    hour: Int
    ): ServiceResult<List<WeightedLatLng>, MapErrorType> {
        // Calculate the boundaries of the query
        val maxLat = centerLat + distance
        val minLat = centerLat - distance
        val maxLng = centerLng + distance
        val minLng = centerLng - distance

        // Format the date
        val formattedDate: String = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        return get(minLat, maxLat, minLng, maxLng, formattedDate, hour)
    }

    /**
     * Get all the documents within the specified boundaries
     * @param minLat the minimum latitude
     * @param maxLat the maximum latitude
     * @param minLng the minimum longitude
     * @param maxLng the maximum longitude
     * @param date the date
     * @param hour the hour
     * @return a ServiceResult object containing the list of Map objects if the operation was successful, an physiologicalError type otherwise
     */
    private suspend fun get(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double, date: String, hour: Int): ServiceResult<List<WeightedLatLng>, MapErrorType> {
        return try {
            val coordinateList = mutableListOf<WeightedLatLng>()

            // Query to get all documents within the specified boundaries
            val querySnapshot = mapCollection
                .whereGreaterThanOrEqualTo("lat", minLat)
                .whereLessThanOrEqualTo("lat", maxLat)
                .whereGreaterThanOrEqualTo("long", minLng)
                .whereLessThanOrEqualTo("long", maxLng)
                .get()
                .await()

            // Iterate over the returned documents and convert the data into Coordinate objects
            querySnapshot.forEach { document ->
                val latitude = (document.get("lat") as Number).toDouble()
                val longitude = (document.get("long") as Number).toDouble()
                val days = document.get("days") as Map<String, Map<String, List<Map<String, Any>>>>

                // Filter the days based on the date
                val filteredDay = days[date] ?: return@forEach

                // Filter the hours based on the specified hour
                val hours = filteredDay["hours"] as? Map<String, Map<String, Any>> ?: return@forEach

                val filteredHourData = hours[hour.toString()] ?: return@forEach

                // Get the stress score from the filtered hour
                val stressScore = (filteredHourData["stress_score"] as Number).toDouble()

                // Build the WeightedLatLng object and add it to the list
                val weightedLatLng = WeightedLatLng(LatLng(latitude, longitude), stressScore)
                coordinateList.add(weightedLatLng)
            }

            Log.d("FirebaseMapDao", "Coordinate list: $coordinateList")
            ServiceResult(true, coordinateList, null)

        } catch (e: Exception) {

            Log.d("FirebaseMapDao", e.toString())
            ServiceResult(false, null, MapErrorType.NETWORK_ERROR)
        }
    }

}