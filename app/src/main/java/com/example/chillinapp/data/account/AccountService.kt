package com.example.chillinapp.data.account

interface AccountService {

    suspend fun createAccount(account: Account): Boolean
  suspend fun signWithGoogle(idToken:String): Boolean


    fun isEmailInUse(email: String): Boolean

    fun credentialAuth(email: String, password: String): Boolean

    fun getAccount(email: String): Account?

//    fun updateAccount(account: Account)
//
//    fun deleteAccount(email: String)

}