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
         try {
            val existingDocument=accountCollection.document(account.email?:"").get().await()
            if(existingDocument.exists()){
                Log.d("Insert in DAO", "Email già presente nella collection")
               return  ServiceResult(false,null,AccountErrorType.EMAIL_IN_USE)
            } else {
                account.password?.let { account.email?.let { it1 -> auth.createUserWithEmailAndPassword(it1, it) } }
                account.email?.let { accountCollection.document(it).set(userData).await() }
                Log.d("Insert in DAO", "Avvenuta con successo")
                return ServiceResult(true,null,null)
            }
        } catch (e: Exception) {
            Log.d("Insert in DAO", e.toString())
             return ServiceResult(false,null,AccountErrorType.DATABASE_ERROR)



        }

    }

    suspend fun isEmailInUse(email: String): ServiceResult<Unit ,AccountErrorType> {

        try {
            val account=accountCollection.document(email).get().await()
            if(account.exists())
                return ServiceResult(true,null,null)
            else
                return ServiceResult(false,null,AccountErrorType.ACCOUNT_NOT_FOUND)

        } catch (e:Exception){
            return ServiceResult(false,null,AccountErrorType.DATABASE_ERROR)
        }
    }

    suspend fun credentialAuth(email: String, password: String): ServiceResult<Unit,AccountErrorType> {

   return  try{
            auth.signInWithEmailAndPassword(email, password).await()
           ServiceResult(true,null,null)
        } catch (e:Exception){
            when (e){
                is FirebaseAuthInvalidCredentialsException-> ServiceResult(false,null,AccountErrorType.INVALID_PASSWORD)
                is FirebaseAuthInvalidUserException->  ServiceResult(false, null, AccountErrorType.ACCOUNT_NOT_FOUND)
                else -> {
                    ServiceResult(false,null,AccountErrorType.DATABASE_ERROR)
                }
            }
        }


}

    suspend fun getAccount(email: String): ServiceResult<Account?, AccountErrorType> {
        try {
            val account=accountCollection.document(email).get().await()
            if(!account.exists())
                return ServiceResult(false,null,AccountErrorType.ACCOUNT_NOT_FOUND)


          return  ServiceResult(true,  Account(account.get("name").toString(), account.get("email").toString(),
              account.get("password").toString()
          ),null)

        } catch (e:Exception){
            return ServiceResult(false,null,AccountErrorType.DATABASE_ERROR)
        }

        }
  //  suspend fun getGoogleIdToken(): String{



  //  }



    suspend fun signInWithGoogle(idToken: String): Boolean {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            auth.signInWithCredential(credential).await()
            true
        } catch (e: Exception) {
            Log.e("AutwithGoogle", "signInWithCredential:failure", e)
            false
        }
    }

    }





