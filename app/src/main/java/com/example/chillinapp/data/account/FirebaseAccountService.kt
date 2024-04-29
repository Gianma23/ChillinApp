package com.example.chillinapp.data.account

import com.example.chillinapp.data.ServiceResult

class FirebaseAccountService(private val accountDao: FirebaseAccountDao): AccountService {

    /*TODO: implement account creation */
    override suspend fun createAccount(account: Account): ServiceResult<Unit, AccountErrorType> =
        accountDao.createAccount(account)

    /*TODO: implement email check */
    override fun isEmailInUse(email: String): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

    /*TODO: implement credential authentication */
    override suspend fun credentialAuth(email: String, encryptedPsw: String): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

    /*TODO: implement account retrieval */
    override fun getAccount(email: String): ServiceResult<Account?, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

    /*TODO: implement google authentication */
    override suspend fun googleAuth(idToken: String): ServiceResult<String, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

    /*TODO: implement password recovery - Not needed (you can leave it like this) */
    override suspend fun recoverPassword(email: String): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

    /*TODO: implement account update - Not needed (you can leave it like this)*/
    override fun updateAccount(account: Account): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

    /*TODO: implement account deletion - Not needed (you can leave it like this)*/
    override fun deleteAccount(email: String): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

}