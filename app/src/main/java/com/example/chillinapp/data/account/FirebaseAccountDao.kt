package com.example.chillinapp.data.account
//noinspection SuspiciousImport
import android.util.Log
import com.example.chillinapp.data.ServiceResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

/**
 * Class that provides an implementation of the AccountDao interface using Firebase.
 *
 * This class provides concrete implementations of the AccountDao methods using Firebase as the backend. It uses
 * Firebase's Firestore database to store account data and Firebase's Authentication service to authenticate users.
 *
 * @property db An instance of FirebaseFirestore used to interact with the Firestore database.
 * @property accountCollection A reference to the "account" collection in the Firestore database.
 * @property auth An instance of Firebase's Authentication service.
 */
class FirebaseAccountDao {

    private val db: FirebaseFirestore = Firebase.firestore
    private val accountCollection = db.collection("account")
    private val auth=Firebase.auth
    private val dbreference=FirebaseDatabase.getInstance("https://chillinapp-a5b5b-default-rtdb.europe-west1.firebasedatabase.app/").reference

    /**
     * Creates a new account in the Firestore database and authenticates the user with Firebase's Authentication service.
     *
     * @param account The account to be created.
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun createAccount(account: Account): ServiceResult<Unit, AccountErrorType> {
        val userData = hashMapOf(
            "email" to account.email,
            "name" to account.name,
            "password" to account.password
        )
        return try {
            val existingDocument = accountCollection.document(account.email ?: "").get().await()
            if (existingDocument.exists()) {
                val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                    success = false,
                    data = null,
                    error = AccountErrorType.EMAIL_IN_USE
                )
                Log.d("FirebaseAccountDao: createAccount", "The account already exists: $account")

                response
            } else {
                account.password?.let {
                    account.email?.let { it1 ->
                        auth.createUserWithEmailAndPassword(
                            it1,
                            it
                        )
                    }
                }
                account.email?.let { accountCollection.document(it).set(userData).await() }


                val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                    success = true,
                    data = null,
                    error = null
                )
                Log.d("FirebaseAccountDao: createAccount", "Account creation successful: $account")
                response
            }
        } catch (e: Exception) {
            Log.e("FirebaseAccountDao: createAccount", "An exception occurred: ", e)
            val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                success = true,
                data = null,
                error = AccountErrorType.DATABASE_ERROR
            )
            Log.d("FirebaseAccountDao: createAccount", "Returning: $account")
            response
        }
    }

    /**
     * Checks if an email is already in use by querying the Firestore database.
     *
     * @param email The email to be checked.
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun isEmailInUse(email: String): ServiceResult<Unit, AccountErrorType> {
        return try {
            val account=accountCollection.document(email).get().await()

            if(account.exists()){
                val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                    success = true,
                    data = null,
                    error = null
                )
                Log.d("FirebaseAccountDao: isEmailInUse", "The email is in use: $email")
                response
            }
            else{
                val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                    success = false,
                    data = null,
                    error = AccountErrorType.ACCOUNT_NOT_FOUND
                )
                Log.d("FirebaseAccountDao: isEmailInUse", "The email is not in use: $email")
                response
            }

        } catch (e:Exception){
            Log.e("FirebaseAccountDao: isEmailInUse", "An exception occurred: ", e)

            val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                success = false,
                data = null,
                error = AccountErrorType.DATABASE_ERROR
            )
            Log.d("FirebaseAccountDao: isEmailInUse", "Returning: $response")
            response
        }
    }

    /**
     * Authenticates credentials with Firebase's Authentication service.
     *
     * @param email The email to be authenticated.
     * @param password The password to be authenticated.
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun credentialAuth(email: String, password: String): ServiceResult<Unit,AccountErrorType> {
        return try {


            auth.signInWithEmailAndPassword(email, password).await()

            val account = getCurrentAccount()?.data
            val name = account?.name


            val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                success = true,
                data = null,
                error = null
            )
            Log.d("FirebaseAccountDao: credentialAuth", "Result: $response")
            response

        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                        success = false,
                        data = null,
                        error = AccountErrorType.INVALID_PASSWORD
                    )
                    Log.d("FirebaseAccountDao: credentialAuth", "Invalid password: $password")
                    response
                }

                is FirebaseAuthInvalidUserException -> {
                    val response: ServiceResult<Unit, AccountErrorType> =
                        ServiceResult(
                            success = false,
                            data = null,
                            error = AccountErrorType.ACCOUNT_NOT_FOUND
                        )
                    Log.d("FirebaseAccountDao: credentialAuth", "The account does not exist: $email")
                    response
                }

                else -> {
                    Log.e("FirebaseAccountDao: credentialAuth", "An exception occurred: ", e)

                    val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                        success = false,
                        data = null,
                        error = AccountErrorType.DATABASE_ERROR
                    )
                    Log.d("FirebaseAccountDao: credentialAuth", "Returning: $response")
                    response
                }
            }
        }
    }

    /**
     * Retrieves an account from the Firestore database.
     *
     * @param email The email of the account to be retrieved.
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun getAccount(email: String): ServiceResult<Account?, AccountErrorType> {
        try {
            val account = accountCollection.document(email).get().await()

            if(!account.exists()) {
                val response: ServiceResult<Account?, AccountErrorType> = ServiceResult(
                    success = false,
                    data = null,
                    error = AccountErrorType.ACCOUNT_NOT_FOUND
                )

                Log.d("FirebaseAccountDao: getAccount", "The account does not exist: $email")
                return response
            }

            val response: ServiceResult<Account?, AccountErrorType> = ServiceResult(
                success = true,
                data = Account(
                    name = account.get("name").toString(),
                    email = account.get("email").toString(),
                    password = account.get("password").toString()
                ),
                error = null
            )

            Log.d("FirebaseAccountDao: getAccount", "Account retrieval successful: $response")
            return response

        } catch (e:Exception){
            Log.e("FirebaseAccountDao: getAccount", "An exception occurred: ", e)

            val response: ServiceResult<Account?, AccountErrorType> = ServiceResult(
                success = false,
                data = null,
                error = AccountErrorType.DATABASE_ERROR
            )

            Log.d("FirebaseAccountDao: getAccount", "Returning: $response")
            return response
        }
    }

    /**
     * Not yet implemented - NOT NEEDED AT THE MOMENT
     */
    //  suspend fun getGoogleIdToken(): String{
    //  }

    /**
     * Authenticates with Google using Firebase's Authentication service.
     *
     * @param idToken The Google ID token to be authenticated.
     * @return A ServiceResult instance containing the result of the operation.
     */
    suspend fun signInWithGoogle(idToken: String): ServiceResult<String, AccountErrorType>{
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            auth.signInWithCredential(credential).await()
            val response: ServiceResult<String, AccountErrorType> = ServiceResult(
                success = true,
                data = idToken,
                error = null
            )
            Log.d("FirebaseAccountDao: signInWithGoogle", "Google sign in successful: $response")
            response
        } catch (e: Exception) {
            Log.e("FirebaseAccountDao: signInWithGoogle", "An exception occurred: ", e)
            val response: ServiceResult<String, AccountErrorType> = ServiceResult(
                success = true,
                data = null,
                error = null
            )
            Log.d("FirebaseAccountDao: signInWithGoogle", "Returning: $response")
            response
        }
    }
    suspend fun getCurrentAccount(): ServiceResult<Account?, AccountErrorType> {
        val currentUser = auth.currentUser
        val currentEmail = currentUser?.email
        Log.d("getCurrentAccount", "Current user: ${currentEmail?.let { getAccount(it) }}")
        return if (currentEmail != null) {
            getAccount(currentEmail)
        } else {
            ServiceResult(
                success = false,
                data = null,
                error = AccountErrorType.ACCOUNT_NOT_FOUND
            )
        }
    }

    /**
     * Signs out the current user from Firebase's Authentication service.
     *
     * @return A ServiceResult instance containing the result of the operation.
     */
    fun signOut(): ServiceResult<Unit,AccountErrorType> {
        return try {
            auth.signOut()
            val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                success = true,
                data = null,
                error = null
            )
            Log.d("FirebaseAccountDao: signOut", "Logout successful")
            response
        } catch (e: Exception) {
            Log.e("FirebaseAccountDao: signOut", "An exception occurred: ", e)
            val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                success = false,
                data = null,
                error = AccountErrorType.AUTHENTICATION_ERROR
            )
            Log.d("FirebaseAccountDao: signOut", "Returning: $response")
            response
        }
    }
    //the user that want to delete his account must be logged
    suspend fun deletecurrentAccount(email: String): ServiceResult<Unit,AccountErrorType>{
        val documentref=accountCollection.document(email)


        try {
          documentref.delete().await()
            auth.currentUser?.delete()
            return ServiceResult(true,null,null)

        } catch(e:Exception){
            return ServiceResult(false,null,AccountErrorType.DATABASE_ERROR)
        }
    }



}





