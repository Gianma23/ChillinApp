package com.example.chillinapp.data.account

class FirebaseAccountRepository(private val accountDao: FirebaseAccountDao): AccountRepository {

    /*TODO: implement account creation */
    override suspend fun createAccount(account: Account): Boolean =
        accountDao.createAccount(account)

    /*TODO: implement email check */
    override fun isEmailInUse(email: String): Boolean = false

    /*TODO: implement credential authentication */
    override fun credentialAuth(email: String, password: String): Boolean = false

    /*TODO: implement account retrieval */
    override fun getAccount(email: String): Account? = null

}