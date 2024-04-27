package com.example.chillinapp.data.account

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FirebaseAccountDao {
    private val db: FirebaseFirestore = Firebase.firestore
    private val accountCollection = db.collection("accounts")

    fun createAccount(account: Account): Boolean {
        /*TODO: implement account creation */
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