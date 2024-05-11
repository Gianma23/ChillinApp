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
    private val auth = Firebase.auth
    private val dbreference =
        FirebaseDatabase.getInstance("https://chillinapp-a5b5b-default-rtdb.europe-west1.firebasedatabase.app/").reference

    /**
     * Insert raw data to the database. Protocol is defined to get 30 samples of data at a time.
     * @param stressData List of [StressRawData] to be inserted
     * @return [ServiceResult] with Unit as success type and [StressErrorType] as physiologicalError type
     */
    suspend fun insertRawData(stressData: List<StressRawData>): ServiceResult<Unit, StressErrorType> {
        val user = auth.currentUser
        val email = user?.email
        val userDocument = email?.let { accountCollection.document(it) }


        return try {
            // Insert raw data for each sample
            for ((i, data) in stressData.withIndex()) {
                val rawDocument = userDocument?.collection("RawData")?.document(stressData[i].timestamp.toString())
                val rawData = hashMapOf(
                    "timestamp" to data.timestamp,
                    "heartrateSensor" to data.heartRateSensor,
                    "skinTemperatureSensor" to data.skinTemperatureSensor,
                    "edaSensor" to data.edaSensor,
                    "latitude" to data.latitude,
                    "longitude" to data.longitude
                )
                Log.d("Insert", "Insert completed")
                rawDocument?.set(rawData)?.await()
            }
            val fastreturn = fastInsert(stressData)
            if (fastreturn.success)
                ServiceResult(true, null, null)
            else {
                fastreturn
            }
        } catch (e: Exception) {
            Log.e("Insert", e.toString())
            ServiceResult(false, null, StressErrorType.NETWORK_ERROR)
        }
    }

    suspend fun insertDerivedData(stressData: List<StressDerivedData>): ServiceResult<Unit, StressErrorType> {
        val user = auth.currentUser
        val email = user?.email
        val userDocument = email?.let { accountCollection.document(it) }
        return try {
            // Insert raw data for each sample
            for ((i, data) in stressData.withIndex()) {
                val rawDocument = userDocument?.collection("DerivedData")?.document(stressData[i].timestamp.toString())
                val derivedData = hashMapOf(
                    "timestamp" to data.timestamp,
                    "binterval" to data.bInterval,
                    "prediction" to data.prediction,
                    "stress_level" to data.stressLevel
                )
                Log.d("Insert", "Insert completed")
                rawDocument?.set(derivedData)?.await()
            }
            ServiceResult(true, null, null)
        } catch (e: Exception) {
            ServiceResult(false, null, StressErrorType.COMMUNICATION_PROBLEM)
        }
    }

    suspend fun getRawData(startTime:Long, endTime:Long): ServiceResult<List<StressRawData>, StressErrorType> {
        val user = auth.currentUser
        val email = user?.email
        val userDocument = email?.let { accountCollection.document(it) }
        val rawDataCollection = userDocument?.collection("RawData")
        return try {
            val rawDataList = mutableListOf<StressRawData>()

            // Effettua una query per ottenere un numero specifico di documenti raw data
            val querySnapshot = rawDataCollection?.whereGreaterThanOrEqualTo("timestamp", startTime)
                ?.whereLessThanOrEqualTo("timestamp", endTime)
                ?.get()?.await()
            // Itera sui documenti restituiti e converte i dati in oggetti StressRawData
            querySnapshot?.forEach { document ->
                val timestamp = document.id.toLongOrNull()
                if (timestamp != null) {
                    val heartrateSensor: Double= (document.get("heartrateSensor") as Double)
                    Log.d("heartsensor", "$heartrateSensor")
                    val skinTemperatureSensor: Double = (document.get("skinTemperatureSensor") as Double)
                    val edaSensor: Double = (document.get("edaSensor") as   Double)

                    // Costruisci l'oggetto StressRawData e aggiungilo alla lista
                    val stressRawData = StressRawData(
                        timestamp = timestamp,
                        heartRateSensor = heartrateSensor.toFloat(),
                        skinTemperatureSensor = skinTemperatureSensor.toFloat(),
                        edaSensor = edaSensor.toFloat()
                    )
                    rawDataList.add(stressRawData)
                }

            }

            ServiceResult(true, rawDataList, null)
        } catch (e: Exception) {
            Log.d("Get Raw Data", e.toString())
            ServiceResult(false, null, StressErrorType.NETWORK_ERROR)


        }
    }

    suspend fun getDerivedData(startTime: Long, endTime: Long): ServiceResult<List<StressDerivedData>, StressErrorType> {
        val user = auth.currentUser
        val email = user?.email
        val userDocument = email?.let { accountCollection.document(it) }
        val derivedDataCollection = userDocument?.collection("RawData")
        return try {
            val derivedDataList = mutableListOf<StressDerivedData>()

            // Effettua una query per ottenere un numero specifico di documenti raw data
            val querySnapshot = derivedDataCollection?.whereGreaterThanOrEqualTo("timestamp", startTime)
                ?.whereLessThanOrEqualTo("timestamp", endTime)
                ?.get()?.await()
            // Itera sui documenti restituiti e converte i dati in oggetti StressRawData
            querySnapshot?.forEach { document ->
                val timestamp = document.id.toLongOrNull()
                if (timestamp != null) {
                    val binterval: Array<Float> = document.get("binterval") as Array<Float>
                    val prediction: Double = (document.get("prediction") as Double)
                    val stress_level: Double = (document.get("stress_level") as Double)

                    // Costruisci l'oggetto StressRawData e aggiungilo alla lista
                    val stressDerivedData = StressDerivedData(timestamp, binterval, prediction, stress_level.toFloat())
                    derivedDataList.add(stressDerivedData)
                }

            }

            ServiceResult(true, derivedDataList, null)
        } catch (e: Exception) {
            ServiceResult(false, null, StressErrorType.NETWORK_ERROR)


        }
    }

    private suspend fun fastInsert(stressData: List<StressRawData>): ServiceResult<Unit, StressErrorType> {
        val email = auth.currentUser?.email
        var key: String? = null
        if (email != null) {
            if(email.contains(".")){
                key = email.replace(".", "")
            }
        }
        val rawdatareference = key?.let { dbreference.child("account").child(it).child("RawData") }
        return if (rawdatareference != null) {
            try {
                rawdatareference.removeValue().await()
                stressData.forEach { data ->
                    rawdatareference.child(data.timestamp.toString()).setValue(data).await()
                }
                Log.d("fastInsert", "Insert completed")
                ServiceResult(success = true, data = null, error = null)
            } catch (e: Exception) {
                Log.e("fastInsert", "An exception occurred", e)
                ServiceResult(success = false, data = null, error = StressErrorType.COMMUNICATION_PROBLEM)
            }
        } else
            ServiceResult(success = false, data = null, error = StressErrorType.NO_ACCOUNT)
    }

    suspend fun fastGet(): ServiceResult<List<StressRawData>, StressErrorType> {
        val user = auth.currentUser
        val email = user?.email
        val keyname = email?.substringBefore("@")
        val rawDataref = keyname?.let { dbreference.child("account").child(it).child("RawData") }
        return try {
            val snapshot = rawDataref?.get()?.await()
            val stressDataList = mutableListOf<StressRawData>()
            snapshot?.children?.forEach { childSnapshot ->
                val timestamp = childSnapshot.key?.toLongOrNull()
                val heartrateSensor = childSnapshot.child("heartrateSensor").value as Float
                val skinTemperatureSensor = childSnapshot.child("skinTemperatureSensor").value as Float
                val edaSensor = childSnapshot.child("edaSensor").value as Float
                if (timestamp != null) {
                    val stressData = StressRawData(timestamp, heartrateSensor, skinTemperatureSensor, edaSensor)
                    stressDataList.add(stressData)
                }
            }

            ServiceResult(success = true, data = stressDataList, error = null)

        } catch (e: Exception) {
            Log.e("readStressDataFromFirebase", "An exception occurred", e)
            ServiceResult(success = false, data = null, error = StressErrorType.COMMUNICATION_PROBLEM)
        }

    }


    suspend fun avgRawData(sincewhen: Long): ServiceResult<StressRawData?,StressErrorType> {
        val auth = Firebase.auth
        val db = Firebase.firestore
        val currentUser = auth.currentUser
        val currentUserEmail = currentUser?.email

        if (currentUserEmail != null) {
            val documentRef = db.document(currentUserEmail).collection("RawData")
            val query = documentRef.whereGreaterThan("timestamp", sincewhen)
            val snapshot = query.get().await()

            var totalHeartSensor = 0.0F
            var totalSkinTemperatureSensor = 0.0F
            var totalEdaSensor = 0.0F
            var count = 0

            snapshot.forEach { document ->
                val heartSensor = document.getDouble("heartSensor")
                val skinTemperatureSensor = document.getDouble("skinTemperatureSensor")
                val edaSensor = document.getDouble("edaSensor")

                if (heartSensor != null && skinTemperatureSensor != null && edaSensor != null) {
                    totalHeartSensor += heartSensor.toFloat()
                    totalSkinTemperatureSensor += skinTemperatureSensor.toFloat()
                    totalEdaSensor += edaSensor.toFloat()
                    count++
                }
            }


            val avgHeartSensor = if (count > 0) totalHeartSensor / count else 0.0F
            val avgSkinTemperatureSensor = if (count > 0) totalSkinTemperatureSensor / count else 0.0F
            val avgEdaSensor = if (count > 0) totalEdaSensor / count else 0.0F
            Log.d("Insert avg", "Completed")
            return  ServiceResult(true,StressRawData(sincewhen,avgHeartSensor, avgSkinTemperatureSensor, avgEdaSensor),null)

        } else {
            Log.d("Insert avg", "not Completed")
            return ServiceResult(false,null, null)
        }
    }

}




   