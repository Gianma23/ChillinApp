package com.example.chillinapp.data.stress

import com.example.chillinapp.data.ServiceResult
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseStressDataDao {
    private val db: FirebaseFirestore = Firebase.firestore
    private val accountCollection = db.collection("account")
    private val auth= Firebase.auth

    /**
     * Insert raw data to the database. Protocol is defined to get 30 samples of data at a time.
     * @param stressData List of [StressRawData] to be inserted
     * @return [ServiceResult] with Unit as success type and [StressErrorType] as error type
     */
    suspend fun insertRawData(stressData: List<StressRawData>): ServiceResult<Unit,StressErrorType>{
        val user=auth.currentUser
        val email= user?.email
        val userDocument= email?.let { accountCollection.document(it) }
        val rawDocument= userDocument?.collection("RawData")?.document(stressData[0].timestamp.toString())

        return try {
            // Insert raw data for each sample
            for (data in stressData){
                val rawData= hashMapOf(
                    //"heartRate" to data.HR,
                    "edaSensor" to data.edaSensor,
                    "skinTemperatureSensor" to data.skinTemperatureSensor,
                )
                rawDocument?.set(rawData)?.await()
            }

            ServiceResult(true,null,null)
        } catch (e:Exception){
            ServiceResult(false,null,StressErrorType.NETWORK_ERROR)
        }
    }
}