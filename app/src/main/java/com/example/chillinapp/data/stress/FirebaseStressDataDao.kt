package com.example.chillinapp.data.stress

import android.util.Log
import com.example.chillinapp.data.ServiceResult
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await



class FirebaseStressDataDao {
    private val db: FirebaseFirestore = Firebase.firestore
    private val accountCollection = db.collection("account")
    private val auth= Firebase.auth
    private val dbReference=FirebaseDatabase.getInstance("https://chillinapp-a5b5b-default-rtdb.europe-west1.firebasedatabase.app/").reference

    /**
     * Insert raw data to the database. Protocol is defined to get 30 samples of data at a time.
     * @param stressData List of [StressRawData] to be inserted
     * @return [ServiceResult] with Unit as success type and [StressErrorType] as error type
     */
    suspend fun insertRawData(stressData: List<StressRawData>): ServiceResult<Unit,StressErrorType>{

        val user=auth.currentUser
        val email= user?.email
        val userDocument= email?.let { accountCollection.document(it) }

        return try {
            // Insert raw data for each sample
            for ((i, data) in stressData.withIndex()){
                val rawDocument= userDocument?.collection("RawData")?.document(stressData[i].timestamp.toString())
                val rawData= hashMapOf(
                    "timestamp" to data.timestamp,
                    "heartRateSensor" to data.heartRateSensor,
                    "skinTemperatureSensor" to data.skinTemperatureSensor,
                )
                Log.d("Insert", "Insert completed")
                rawDocument?.set(rawData)?.await()
            }

            val fastReturn = fastInsert(stressData)
            if (fastReturn.success)
                ServiceResult(true,null,null)
            else
                fastReturn

        } catch (e:Exception){
            Log.e("Insert", "An exception occurred", e)
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

            // Get the last n samples of raw data
            val querySnapshot = rawDataCollection?.limit(n.toLong())?.get()?.await()

            // Iterate over the query results and create a StressRawData object for each document
            querySnapshot?.forEach { document ->
                val timestamp = document.id.toLongOrNull()
                if (timestamp != null) {
                    val heartRateSensor:Double = (document.get("heartRateSensor") as Double)
                    val skinTemperatureSensor : Double= (document.get("skinTemperatureSensor" ) as Double)

                    // Generate the StressRawData object and add it to the list
                    val stressRawData = StressRawData(timestamp, heartRateSensor, skinTemperatureSensor)
                    rawDataList.add(stressRawData)
                }
            }

            val response: ServiceResult<List<StressRawData>, StressErrorType> =
                ServiceResult(
                    success = true,
                    data = rawDataList,
                    error = null
                )
            Log.d("getRawData", "Data retrieved successfully")
            response
        } catch (e: Exception) {
            Log.e("getRawData", "An exception occurred", e)
            ServiceResult(false, null, StressErrorType.NETWORK_ERROR)
        }
    }

    suspend fun fastInsert(stressData: List<StressRawData>) :ServiceResult<Unit,StressErrorType>{
        val email= auth.currentUser?.email
        val key= email?.substringBefore("@")
        val rawDataReference= key?.let { dbReference.child(it).child("RawData") }
        if (rawDataReference != null) {
            return try {
                rawDataReference.removeValue().await()
                stressData.forEach{data->
                rawDataReference.child(data.timestamp.toString()).setValue(data).await()
                }
                Log.d("fastInsert", "Insert completed")
                ServiceResult(success = true, data = null, error = null)
            }  catch (e: Exception) {
                Log.e("fastInsert", "An exception occurred", e)
                ServiceResult(success = false, data = null, error = StressErrorType.COMMUNICATION_PROBLEM)
            }
        }
       else {
            Log.e("fastInsert", "No account found")
            return ServiceResult(success = false, data = null, error = StressErrorType.NO_ACCOUNT)
        }
    }

    suspend fun fastGet():ServiceResult<List<StressRawData>,StressErrorType>{
        val user = auth.currentUser
        val email = user?.email
        val keyName = email?.substringBefore("@")
        val rawDataRef = keyName?.let { dbReference.child(it).child("RawData") }
        return try {
            val snapshot= rawDataRef?.get()?.await()
            val stressDataList= mutableListOf<StressRawData>()
            snapshot?.children?.forEach{ childSnapshot->
                val timestamp=childSnapshot.key?.toLongOrNull()
                val heartRateSensor=childSnapshot.child("heartRateSensor").value as Double
                val skinTemperatureSensor=childSnapshot.child("skinTemperatureSensor").value as Double
                if (timestamp != null) {
                    val stressData = StressRawData(timestamp, heartRateSensor, skinTemperatureSensor)
                    stressDataList.add(stressData)
                }
            }

            ServiceResult(success = true, data = stressDataList, error = null)

            }
        catch (e: Exception) {
            Log.e("readStressDataFromFirebase", "An exception occurred", e)
            ServiceResult(success = false, data = null, error = StressErrorType.COMMUNICATION_PROBLEM)
        }
    }

}