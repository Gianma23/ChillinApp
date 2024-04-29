package com.example.chillinapp.data.account

import com.example.chillinapp.data.ServiceResult

class FirebaseAccountService(private val accountDao: FirebaseAccountDao): AccountService {


    override suspend fun createAccount(account: Account): ServiceResult<Unit, AccountErrorType> =
        accountDao.createAccount(account)


    override suspend fun isEmailInUse(email: String): ServiceResult<Unit, AccountErrorType> =
        accountDao.isEmailInUse(email)


    override suspend fun credentialAuth(email: String, encryptedPsw: String): ServiceResult<Unit, AccountErrorType> =
       accountDao.credentialAuth(email, encryptedPsw)


    override suspend fun getAccount(email: String): ServiceResult<Account?, AccountErrorType> =
       accountDao.getAccount(email)

    ovveride suspend fun getGooogleIdToken(): ServiceResult<String?,AccountErrorType>=
    override suspend fun googleAuth(idToken: String): ServiceResult<String, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

    override fun recoverPassword(email: String): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

}