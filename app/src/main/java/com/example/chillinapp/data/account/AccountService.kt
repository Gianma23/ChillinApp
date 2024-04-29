package com.example.chillinapp.data.account

import com.example.chillinapp.data.ServiceResult

interface AccountService {

    suspend fun createAccount(account: Account): ServiceResult<Unit, AccountErrorType>

    suspend fun isEmailInUse(email: String): ServiceResult<Unit, AccountErrorType>

    suspend fun credentialAuth(email: String, encryptedPsw: String): ServiceResult<Unit, AccountErrorType>

    suspend fun getAccount(email: String): ServiceResult<Account?, AccountErrorType>
    suspend fun getGoogleIdToken():ServiceResult<String?,AccountErrorType>

    suspend fun googleAuth(idToken: String): ServiceResult<String, AccountErrorType>

    fun recoverPassword(email: String): ServiceResult<Unit, AccountErrorType>

//    fun updateAccount(account: Account)
//
//    fun deleteAccount(email: String)

}