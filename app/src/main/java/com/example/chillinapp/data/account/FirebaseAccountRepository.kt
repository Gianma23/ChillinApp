package com.example.chillinapp.data.account

class FirebaseAccountRepository(private val accountDao: FirebaseAccountDao): AccountRepository {

    override fun createAccount(account: Account): Boolean =
        accountDao.createAccount(account)

    override fun isEmailInUse(email: String): Boolean =
        accountDao.isEmailInUse(email)

    override fun credentialAuth(email: String, password: String): Boolean =
        accountDao.credentialAuth(email, password)

    override fun getAccount(email: String): Account? =
        accountDao.getAccount(email)

}