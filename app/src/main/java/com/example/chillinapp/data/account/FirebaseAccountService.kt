package com.example.chillinapp.data.account

import com.example.chillinapp.data.ServiceResult

/**
 * Class that provides an implementation of the AccountService interface using Firebase.
 *
 * This class implements the AccountService interface and provides concrete implementations of its methods using
 * Firebase as the backend. It uses an instance of FirebaseAccountDao to interact with the Firebase database.
 *
 * @property accountDao An instance of FirebaseAccountDao used to interact with the Firebase database.
 */
class FirebaseAccountService(private val accountDao: FirebaseAccountDao): AccountService {

    /**
     * Creates a new account using the FirebaseAccountDao.
     *
     * @param account The account to be created.
     * @return A ServiceResult instance containing the result of the operation.
     */
    override suspend fun createAccount(account: Account): ServiceResult<Unit, AccountErrorType> =
        accountDao.createAccount(account)

    /**
     * Checks if an email is already in use using the FirebaseAccountDao.
     *
     * @param email The email to be checked.
     * @return A ServiceResult instance containing the result of the operation.
     */
    override suspend fun isEmailInUse(email: String): ServiceResult<Unit, AccountErrorType> =
        accountDao.isEmailInUse(email)

    /**
     * Authenticates credentials using the FirebaseAccountDao.
     *
     * @param email The email to be authenticated.
     * @param encryptedPsw The encrypted password to be authenticated.
     * @return A ServiceResult instance containing the result of the operation.
     */
    override suspend fun credentialAuth(email: String, encryptedPsw: String): ServiceResult<Unit, AccountErrorType> =
       accountDao.credentialAuth(email, encryptedPsw)

    /**
     * Gets an account using the FirebaseAccountDao.
     *
     * @param email The email of the account to be retrieved.
     * @return A ServiceResult instance containing the result of the operation.
     */
    override suspend fun getAccount(email: String): ServiceResult<Account?, AccountErrorType> =
       accountDao.getAccount(email)

    /**
     * Gets a Google ID token. This method is not yet implemented and always returns a ServiceResult indicating failure.
     *
     * @return A ServiceResult instance containing the result of the operation.
     */
    override suspend fun getGoogleIdToken(): ServiceResult<String?,AccountErrorType> = ServiceResult(
        success = false,
        data = null,
        error = AccountErrorType.NOT_YET_IMPLEMENTED
    )

    /**
     * Authenticates with Google. This method is not yet implemented and always returns a ServiceResult indicating failure.
     *
     * @param idToken The Google ID token to be authenticated.
     * @return A ServiceResult instance containing the result of the operation.
     */
    override suspend fun googleAuth(idToken: String): ServiceResult<String, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

    /**
     * Recovers a password. This method is not yet implemented and always returns a ServiceResult indicating failure.
     *
     * @param email The email of the account for which to recover the password.
     * @return A ServiceResult instance containing the result of the operation.
     */
    override suspend fun recoverPassword(email: String): ServiceResult<Unit, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

    /**
     * Updates an account. This method is not yet implemented and always returns a ServiceResult indicating failure.
     *
     * @param account The account to be updated.
     * @return A ServiceResult instance containing the result of the operation.
     */
    override fun updateAccount(account: Account): ServiceResult<Boolean, AccountErrorType> =
        ServiceResult(
            success = false,
            data = null,
            error = AccountErrorType.NOT_YET_IMPLEMENTED
        )

    /**
     * Deletes an account.
     *
     * @return A ServiceResult instance containing the result of the operation.
     */
    override suspend fun deleteAccount(): ServiceResult<Unit, AccountErrorType> =
       accountDao.deletecurrentAccount()

    /**
     * Gets the current account using the FirebaseAccountDao.
     *
     * @return A ServiceResult instance containing the result of the operation.
     */
    override suspend fun getCurrentAccount(): ServiceResult<Account?, AccountErrorType> =
        accountDao.getCurrentAccount()

    /**
     * Signs out the current user using the FirebaseAccountDao.
     *
     * @return A ServiceResult instance containing the result of the operation.
     */
    override fun signOut(): ServiceResult<Unit,AccountErrorType> =
        accountDao.signOut()
}