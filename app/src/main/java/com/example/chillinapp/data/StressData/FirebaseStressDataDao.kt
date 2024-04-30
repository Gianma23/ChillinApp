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
    suspend fun InsertRawData(stressData:StressRawData): ServiceResult<Unit,StressErrorType>{
        val user=auth.currentUser
        val email= user?.email
        val userDocument= email?.let { accountCollection.document(it) }
        val rawDocument= userDocument?.collection("RawData")?.document(stressData.timeStamp.toString())

        try {
            val rawData= hashMapOf(
                "HR" to stressData.HR,
                "GDR" to stressData.GDR,
                "TEMP" to stressData.TEMP,
            )
            rawDocument?.set(rawData)?.await()
            return ServiceResult(true,null,null)

        } catch (e:Exception){
            return ServiceResult(false,null,StressErrorType.NETWORK_ERROR)
        }
    }
}