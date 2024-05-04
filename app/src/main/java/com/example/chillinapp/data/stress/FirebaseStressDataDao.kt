package com.example.chillinapp.data.stress

import android.util.Log
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.data.account.FirebaseAccountDao
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
    private val dbreference=FirebaseDatabase.getInstance("https://chillinapp-a5b5b-default-rtdb.europe-west1.firebasedatabase.app/").reference

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
            var i:Int=0
            // Insert raw data for each sample
            for (data in stressData){
                val rawDocument= userDocument?.collection("RawData")?.document(stressData[i].timestamp.toString())
                val rawData= hashMapOf(
                    "timestamp" to data.timestamp,
                    "heartrateSensor" to data.heartrateSensor,
                    "skinTemperatureSensor" to data.skinTemperatureSensor,
                )
                Log.d("Insert", "Insert completed")
                rawDocument?.set(rawData)?.await()
                i++


            }

            val fastreturn=fastInsert(stressData)
            if (fastreturn.success)
            ServiceResult(true,null,null)
            else
                fastreturn
        } catch (e:Exception){
            ServiceResult(false,null,StressErrorType.NETWORK_ERROR)

        }
    }
    suspend fun insertDerivedData(stressData: List<StressDerivedData>) : ServiceResult<Unit,StressErrorType> {
        val user = auth.currentUser
        val email = user?.email
        val userDocument = email?.let { accountCollection.document(it) }
        return try {
            var i: Int = 0
            // Insert raw data for each sample
            for (data in stressData) {
                val rawDocument = userDocument?.collection("DerivedData")?.document(stressData[i].timestamp.toString())
                val derivedData = hashMapOf(
                    "timestamp" to data.timestamp,
                    "binterval" to data.BINTERVAL,
                    "prediction" to data.prediction,
                    "stress_level" to data.stressLevel
                )
                Log.d("Insert", "Insert completed")
                rawDocument?.set(derivedData)?.await()
                i++


            }
            ServiceResult(true, null, null)


        } catch (e:Exception){
            ServiceResult(false,null,StressErrorType.COMMUNICATION_PROBLEM)
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
                    val skinTemperatureSensor : Double= (document.get("skinTemperatureSensor" ) as Double)

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
    suspend fun getDerivedData(n: Int): ServiceResult <List <StressDerivedData>,StressErrorType> {
        val user = auth.currentUser
        val email = user?.email
        val userDocument = email?.let { accountCollection.document(it) }
        val derivedDataCollection = userDocument?.collection("RawData")
        return try {
            val derivedDataList = mutableListOf<StressDerivedData>()

            // Effettua una query per ottenere un numero specifico di documenti raw data
            val querySnapshot = derivedDataCollection?.limit(n.toLong())?.get()?.await()

            // Itera sui documenti restituiti e converte i dati in oggetti StressRawData
            querySnapshot?.forEach { document ->
                val timestamp = document.id.toLongOrNull()
                if (timestamp != null) {
                    val binterval:Array<Float> = (document.get("binterval") as Array<Float>)
                    val prediction : Double= (document.get("preditiction" ) as Double)
                    val stress_level:Float=(document.get("stress_level") as Float)

                    // Costruisci l'oggetto StressRawData e aggiungilo alla lista
                    val stressDerivedData = StressDerivedData(timestamp,binterval, prediction, stress_level)
                    derivedDataList.add(stressDerivedData)
                }

            }

            ServiceResult(true, derivedDataList, null)
        } catch (e: Exception) {
            ServiceResult(false, null, StressErrorType.NETWORK_ERROR)


        }
    }
    private suspend fun fastInsert(stressData: List<StressRawData>) :ServiceResult<Unit,StressErrorType>{
        val email= auth.currentUser?.email
        val key= email?.substringBefore("@")
        val rawdatareference= key?.let { dbreference.child(it).child("RawData") }
        if (rawdatareference != null) {
            return try {
                rawdatareference.removeValue().await()
                stressData.forEach{data->
                rawdatareference.child(data.timestamp.toString()).setValue(data).await()
                }
                Log.d("fastInsert", "Insert completed")
                ServiceResult(success = true, data = null, error = null)
            }  catch (e: Exception) {
                Log.e("fastInsert", "An exception occurred", e)
                ServiceResult(success = false, data = null, error = StressErrorType.COMMUNICATION_PROBLEM)
            }
        }
       else
           return ServiceResult(success = false, data = null, error = StressErrorType.NOACCOUNT)
    }
    suspend fun fastget():ServiceResult<List<StressRawData>,StressErrorType>{
        val user=auth.currentUser
        val email=user?.email
        val keyname= email?.substringBefore("@")
        val rawDataref= keyname?.let { dbreference.child(it).child("RawData") }
        return try {
            val snapshot= rawDataref?.get()?.await()
            val stressDataList= mutableListOf<StressRawData>()
            snapshot?.children?.forEach{ childSnapshot->
                val timestamp=childSnapshot.key?.toLongOrNull()
                val heartrateSensor=childSnapshot.child("heartrateSensor").getValue() as Float
                val skinTemperatureSensor=childSnapshot.child("skinTemperatureSensor").getValue() as Double
                if (timestamp != null && heartrateSensor != null && skinTemperatureSensor != null) {
                    val stressData = StressRawData(timestamp, heartrateSensor, skinTemperatureSensor)
                    stressDataList.add(stressData)
                }
            }

            ServiceResult(success = true, data = stressDataList, error = null)

        } catch (e: Exception) {
            Log.e("readStressDataFromFirebase", "An exception occurred", e)
            ServiceResult(success = false, data = null, error = StressErrorType.COMMUNICATION_PROBLEM)
        }

    }
}