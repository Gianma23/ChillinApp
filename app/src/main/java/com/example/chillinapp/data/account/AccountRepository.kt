package com.example.chillinapp.data.account

import com.example.chillinapp.data.ServiceResult

interface AccountRepository {

    fun createAccount(account: Account): ServiceResult<Unit, AccountErrorType>

    fun isEmailInUse(email: String): ServiceResult<Unit, AccountErrorType>

    fun credentialAuth(email: String, password: String): ServiceResult<Unit, AccountErrorType>

    fun getAccount(email: String): ServiceResult<Account?, AccountErrorType>

//    fun updateAccount(account: Account)
//
//    fun deleteAccount(email: String)

}