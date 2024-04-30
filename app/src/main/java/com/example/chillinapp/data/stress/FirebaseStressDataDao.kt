package com.example.chillinapp.data.stress

import android.util.Log
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
                    "heartrateSensor" to data.heartrateSensor,
                    "skinTemperatureSensor" to data.skinTemperatureSensor,
                )
                Log.d("Insert", "Insert completed")
                rawDocument?.set(rawData)?.await()

            }


            ServiceResult(true,null,null)
        } catch (e:Exception){
            ServiceResult(false,null,StressErrorType.NETWORK_ERROR)

        }
    }
    suspend fun getRawData(n: Int): ServiceResult <List <StressRawData>,StressErrorType> {
        val user = auth.currentUser
        val email = user?.email
        val userDocument = email?.let { accountCollection.document(it) }
        val rawDataCollection = userDocument?.collection("RawData")
        return try {
            val rawDataList = mutableListOf<StressRawData>()

            // Effettua una query per ottenere un numero specifico di documenti raw data
            val querySnapshot = rawDataCollection?.limit(n.toLong())?.get()?.await()

            // Itera sui documenti restituiti e converte i dati in oggetti StressRawData
            querySnapshot?.forEach { document ->
                val timestamp = document.id.toLongOrNull()
                if (timestamp != null) {
                    val heartrateSensor:Float = (document.get("heartrateSensor") as Float)
                    val skinTemperatureSensor : Float= (document.get("skinTemperatureSensor" ) as Float)

                    // Costruisci l'oggetto StressRawData e aggiungilo alla lista
                    val stressRawData = StressRawData(timestamp, heartrateSensor, skinTemperatureSensor)
                    rawDataList.add(stressRawData)
                }
            }

            ServiceResult(true, rawDataList, null)
        } catch (e: Exception) {
            ServiceResult(false, null, StressErrorType.NETWORK_ERROR)


        }
    }
    }