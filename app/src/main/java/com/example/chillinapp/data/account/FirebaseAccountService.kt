package com.example.chillinapp.data.account

class FirebaseAccountService(private val accountDao: FirebaseAccountDao): AccountService {

    /*TODO: implement account creation */
    override suspend fun createAccount(account: Account): Boolean =
        accountDao.createAccount(account)
  override suspend fun signWithGoogle(idToken: String): Boolean=
        accountDao.signInWithGoogle(idToken)


    /*TODO: implement email check */
    override fun isEmailInUse(email: String): Boolean = false

    /*TODO: implement credential authentication */
    override fun credentialAuth(email: String, password: String): Boolean = false

    /*TODO: implement account retrieval */
    override fun getAccount(email: String): Account? = null

}