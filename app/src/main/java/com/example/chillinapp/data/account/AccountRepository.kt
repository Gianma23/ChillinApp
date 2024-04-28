package com.example.chillinapp.data.account

interface AccountRepository {

    fun createAccount(account: Account): Boolean

    fun isEmailInUse(email: String): Boolean

    fun credentialAuth(email: String, password: String): Boolean

    fun getAccount(email: String): Account?

//    fun updateAccount(account: Account)
//
//    fun deleteAccount(email: String)

}