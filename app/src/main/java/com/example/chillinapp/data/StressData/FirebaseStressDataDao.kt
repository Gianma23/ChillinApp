package com.example.chillinapp.data.StressData

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
    suspend fun InsertData(stressData:StressData): ServiceResult<Unit,StressErrorType>{
        val user=auth.currentUser
        val email= user?.email
        val userDocument= email?.let { accountCollection.document(it) }
        val rawDocument= userDocument?.collection("RawData")?.document(stressData.timeStamp.toString())
        val derivedDocument= userDocument?.collection("DerivedData")?.document(stressData.timeStamp.toString())
        try {
            val rawData= hashMapOf(
                "HR" to stressData.HR,
                "GDR" to stressData.GDR,
                "TEMP" to stressData.TEMP,
            )
            val derivedData= hashMapOf(
                "BINTERVAL" to stressData.BINTERVAL,
                "excpected_prediction" to stressData.prediction,
                "STRESS_LEVEL" to stressData.stressLevel
            )
            rawDocument?.set(rawData)?.await()
            derivedDocument?.set(derivedData)?.await()
            return ServiceResult(true,null,null)




        } catch (e:Exception){
            return ServiceResult(false,null,StressErrorType.NETWORK_ERROR)
        }
    }
}