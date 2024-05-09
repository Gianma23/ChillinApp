package com.example.chillinapp.data.account

import com.example.chillinapp.data.ServiceResult

/**
 * Interface for the AccountService which is used for account related operations in the application.
 *
 * This interface defines the methods that any AccountService implementation must provide. These methods include
 * operations for creating an account, checking if an email is in use, authenticating credentials, getting an account,
 * getting a Google ID token, authenticating with Google, recovering a password, updating an account, and deleting an account.
 *
 * All methods are suspend functions, meaning they are designed to be used with Kotlin's coroutines and can perform
 * long-running operations such as network requests or database queries without blocking the main thread.
 *
 * Each method returns a ServiceResult instance which encapsulates the result of the operation. The ServiceResult
 * instance contains a success flag, data (if the operation was successful), and error information (if the operation failed).
 */
interface AccountService {

    /**
     * Creates a new account.
     *
     * @param account The account to be created.
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun createAccount(account: Account): ServiceResult<Unit, AccountErrorType>

    /**
     * Checks if an email is already in use.
     *
     * @param email The email to be checked.
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun isEmailInUse(email: String): ServiceResult<Unit, AccountErrorType>

    /**
     * Authenticates credentials.
     *
     * @param email The email to be authenticated.
     * @param encryptedPsw The encrypted password to be authenticated.
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun credentialAuth(email: String, encryptedPsw: String): ServiceResult<Unit, AccountErrorType>

    /**
     * Gets an account.
     *
     * @param email The email of the account to be retrieved.
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun getAccount(email: String): ServiceResult<Account?, AccountErrorType>

    /**
     * Gets a Google ID token.
     *
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun getGoogleIdToken():ServiceResult<String?,AccountErrorType>

    /**
     * Authenticates with Google.
     *
     * @param idToken The Google ID token to be authenticated.
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun googleAuth(idToken: String): ServiceResult<String, AccountErrorType>

    /**
     * Recovers a password.
     *
     * @param email The email of the account for which to recover the password.
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun recoverPassword(email: String): ServiceResult<Unit, AccountErrorType>

    /**
     * Updates an account.
     *
     * @param account The account to be updated.
     * @return A ServiceResult instance containing the result of the operation.
     */
    fun updateAccount(account: Account): ServiceResult<Boolean, AccountErrorType>

    /**
     * Deletes an account.
     *
     * @param email The email of the account to be deleted.
     * @return A ServiceResult instance containing the result of the operation.
     */
    fun deleteAccount(email: String): ServiceResult<Unit, AccountErrorType>

    /**
     * Signs out the current user.
     *
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun getCurrentAccount() : ServiceResult <Account?, AccountErrorType>
    fun signOut(): ServiceResult<Unit, AccountErrorType>

}