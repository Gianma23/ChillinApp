package com.example.chillinapp.data.map

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.data.stress.StressRawData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseMapDao {

    private val db: FirebaseFirestore = Firebase.firestore
    private val mapCollection = db.collection("Map")

    /**
     * Get all the documents in the Map collection
     * @return a ServiceResult object containing the list of Map objects if the operation was successful, an error type otherwise
     */
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
    }

    /**
     * Get all the documents within the specified boundaries
     * @param minLat the minimum latitude
     * @param maxLat the maximum latitude
     * @param minLng the minimum longitude
     * @param maxLng the maximum longitude
     * @return a ServiceResult object containing the list of Map objects if the operation was successful, an error type otherwise
     */
    suspend fun get(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): ServiceResult<List<Map>, MapErrorType> {
        return try {
            val mapList = mutableListOf<Map>()

            // Query to get all documents within the specified boundaries
            val querySnapshot = mapCollection
                .whereGreaterThanOrEqualTo("latitude", minLat)
                .whereLessThanOrEqualTo("latitude", maxLat)
                .whereGreaterThanOrEqualTo("longitude", minLng)
                .whereLessThanOrEqualTo("longitude", maxLng)
                .get()
                .await()

            // Iterate over the returned documents and convert the data into Map objects
            querySnapshot.forEach { document ->
                val latitude = document.get("latitude") as Double
                val longitude = document.get("longitude") as Double
                val stressScore = document.get("stressScore") as Float

                // Build the Map object and add it to the list
                val map = Map(latitude, longitude, stressScore)
                mapList.add(map)
            }

            ServiceResult(true, mapList, null)
        } catch (e: Exception) {
            ServiceResult(false, null, MapErrorType.NETWORK_ERROR)
        }
    }

}