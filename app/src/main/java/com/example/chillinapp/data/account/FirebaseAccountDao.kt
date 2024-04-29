package com.example.chillinapp.data.account

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseAccountDao {
    private val db: FirebaseFirestore = Firebase.firestore
    private val accountCollection = db.collection("account")
    private val auth=Firebase.auth

    suspend fun createAccount(account: Account): Boolean {
        val userData = hashMapOf(
            "email" to account.email,
            "name" to account.name,
            "password" to account.password
        )
        try {
            val existngDocument=accountCollection.document(account.email?:"").get().await()
            if(existngDocument.exists()){
                Log.d("Insert in DAO", "Email già presente nella collection")
                return false
            } else {
                account.password?.let { account.email?.let { it1 -> auth.createUserWithEmailAndPassword(it1, it) } }
                account.email?.let { accountCollection.document(it).set(userData).await() }
                Log.d("Insert in DAO", "Avvenuta con successo")
                return true
            }
        } catch (e: Exception) {

            throw e
        }

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
    suspend fun signInWithGoogle(idToken: String): Boolean {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        try {
            auth.signInWithCredential(credential).await()
            return true
        } catch (e: Exception) {
            Log.e("AutwithGoogle", "signInWithCredential:failure", e)
            return false
        }
    }

    }




