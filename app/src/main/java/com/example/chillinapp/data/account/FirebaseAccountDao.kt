package com.example.chillinapp.data.account
//noinspection SuspiciousImport
import android.util.Log
import com.example.chillinapp.data.ServiceResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseAccountDao {
    private val db: FirebaseFirestore = Firebase.firestore
    private val accountCollection = db.collection("account")
    private val auth=Firebase.auth


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

    suspend fun credentialAuth(email: String, password: String): ServiceResult<Unit,AccountErrorType> {
        return try{

            auth.signInWithEmailAndPassword(email, password).await()

            val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                success = true,
                data = null,
                error = null
            )
            Log.d("FirebaseAccountDao: credentialAuth", "Credential authentication successful: $response")
            response

        } catch (e:Exception){
            when (e){
                is FirebaseAuthInvalidCredentialsException-> {
                    val response: ServiceResult<Unit, AccountErrorType> = ServiceResult(
                        success = false,
                        data = null,
                        error = AccountErrorType.INVALID_PASSWORD
                    )
                    Log.d("FirebaseAccountDao: credentialAuth", "Invalid password: $password")
                    response
                }
                is FirebaseAuthInvalidUserException->  { val response: ServiceResult<Unit, AccountErrorType> =
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
     * Not yet implemented - NOT NEEDED AT THE MOMENT
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
    suspend fun signOut(): ServiceResult<Unit,AccountErrorType> {
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


}





