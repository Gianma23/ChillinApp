package com.example.chillinapp.data.account

class FirebaseAccountRepository(): AccountRepository {

    /*TODO: implement account creation */
    override fun createAccount(account: Account): Boolean = false

    /*TODO: implement email check */
    override fun isEmailInUse(email: String): Boolean = false

    /*TODO: implement credential authentication */
    override fun credentialAuth(email: String, password: String): Boolean = false

    /*TODO: implement account retrieval */
    override fun getAccount(email: String): Account? = null

}