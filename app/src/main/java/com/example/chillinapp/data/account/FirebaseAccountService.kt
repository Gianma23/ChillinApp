package com.example.chillinapp.data.account

import com.example.chillinapp.data.ServiceResult

class FirebaseAccountService(private val accountDao: FirebaseAccountDao): AccountService {

    /*TODO: implement account creation */
    override fun createAccount(account: Account): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false
        )

    /*TODO: implement email check */
    override fun isEmailInUse(email: String): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false
        )

    /*TODO: implement credential authentication */
    override fun credentialAuth(email: String, encryptedPsw: String): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false
        )

    /*TODO: implement account retrieval */
    override fun getAccount(email: String): ServiceResult<Account?, AccountErrorType> =
        ServiceResult(
            success = false
        )

    /*TODO: implement google authentication */
    override fun googleAuth(): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false
        )

    /*TODO: implement password recovery - Not needed (you can leave it like this) */
    override fun recoverPassword(email: String): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false
        )

}