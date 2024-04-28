package com.example.chillinapp.data.account

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseAccountDao {
    private val db: FirebaseFirestore = Firebase.firestore
    private val accountCollection = db.collection("account")

    suspend fun createAccount(account: Account): Boolean {
        val userData = hashMapOf(
            "email" to account.email,
            "name" to account.name,
            "password" to account.password
        )
        try {
            account.email?.let { accountCollection.document(it).set(userData).await() }
            Log.d("Insert in DAO", "Avvenuta con successo")
        } catch (e: Exception) {

            throw e
        }
        return false
    }

    fun isEmailInUse(email: String): Boolean {
        /*TODO: implement email check */
        return false
    }

    fun credentialAuth(email: String, password: String): Boolean {
        /*TODO: implement credential authentication */
        return false
    }

    fun getAccount(email: String): Account? {
        /*TODO: implement account retrieval */
        return null
    }


}