package com.example.chillinapp.data.account

import android.util.Log
import com.example.chillinapp.data.ServiceResult
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseAccountDao {
    private val db: FirebaseFirestore = Firebase.firestore
    private val accountCollection = db.collection("account")
    private val auth=Firebase.auth

    suspend fun createAccount(account: Account): ServiceResult<Unit, AccountErrorType> {
        val userData = hashMapOf(
            "email" to account.email,
            "name" to account.name,
            "password" to account.password
        )
        return try {
            val existingDocument=accountCollection.document(account.email?:"").get().await()
            if(existingDocument.exists()){
                val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                    success = false,
                    data = null,
                    error = AccountErrorType.EMAIL_IN_USE
                )
                Log.d("FirebaseAccountDao: createAccount", "The account already exists: $account")
                response
            } else {
                account.password?.let { account.email?.let { it1 -> auth.createUserWithEmailAndPassword(it1, it) } }
                account.email?.let { accountCollection.document(it).set(userData).await() }
                val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                    success = true,
                    data = null,
                    error = null
                )
                Log.d("FirebaseAccountDao: createAccount", "Account creation successful: $account")
                response
            }
        } catch (e: Exception) {
            Log.e("FirebaseAccountDao: createAccount", "An exception occurred: ", e)
            val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                success = true,
                data = null,
                error = null
            )
            Log.d("FirebaseAccountDao: createAccount", "Returning: $account")
            response
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
    suspend fun signInWithGoogle(idToken: String): ServiceResult<String, AccountErrorType>{
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            auth.signInWithCredential(credential).await()
            val response: ServiceResult<String, AccountErrorType> = ServiceResult(
                success = true,
                data = idToken,
                error = null
            )
            Log.d("FirebaseAccountDao: signInWithGoogle", "Google sign in successful: $response")
            response
        } catch (e: Exception) {
            Log.e("FirebaseAccountDao: signInWithGoogle", "An exception occurred: ", e)
            val response: ServiceResult<String, AccountErrorType> = ServiceResult(
                success = true,
                data = null,
                error = null
            )
            Log.d("FirebaseAccountDao: signInWithGoogle", "Returning: $response")
            response
        }
    }

    }




