package com.example.chillinapp.data.account

import com.example.chillinapp.data.ServiceResult

interface AccountService {

    suspend fun createAccount(account: Account): ServiceResult<Unit, AccountErrorType>

    fun isEmailInUse(email: String): ServiceResult<Unit, AccountErrorType>

    suspend fun credentialAuth(email: String, encryptedPsw: String): ServiceResult<Unit, AccountErrorType>

    fun getAccount(email: String): ServiceResult<Account?, AccountErrorType>

    suspend fun googleAuth(idToken: String): ServiceResult<String, AccountErrorType>

    suspend fun recoverPassword(email: String): ServiceResult<Unit, AccountErrorType>

    fun updateAccount(account: Account): ServiceResult<Unit, AccountErrorType>

    fun deleteAccount(email: String): ServiceResult<Unit, AccountErrorType>

}