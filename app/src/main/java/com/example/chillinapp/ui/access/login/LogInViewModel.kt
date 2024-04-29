package com.example.chillinapp.ui.access.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.ui.access.utility.AccessStatus
import com.example.chillinapp.ui.access.utility.EmailValidationResult
import com.example.chillinapp.ui.access.utility.PasswordValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LogInViewModel(private val accountService: AccountService): ViewModel() {

    private val _uiState = MutableStateFlow(LogInUiState())
    val uiState: StateFlow<LogInUiState> = _uiState.asStateFlow()
    
    companion object{
        private const val MAX_INPUT_LENGTH: Int = 254
    }

    fun updateEmail(email: String) {

        idleAccessStatus()

        _uiState.value = _uiState.value.copy(
            email = email,
            emailStatus = emailValidation(email)
        )

        updateLogInButton()
        
        Log.d("LogInViewModel", "Email: ${_uiState.value.email} Validity: ${_uiState.value.emailStatus}")

    }
    
    private fun emailValidation(email: String): EmailValidationResult {
        return when {
            email.isEmpty() -> EmailValidationResult.EMPTY
            email.length > MAX_INPUT_LENGTH -> EmailValidationResult.TOO_LONG
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> EmailValidationResult.INVALID_FORMAT
            else -> EmailValidationResult.VALID
        }
    }

    fun updatePassword(password: String) {

        idleAccessStatus()

        _uiState.value = _uiState.value.copy(
            password = password,
            passwordStatus = passwordValidation(password)
        )
        
        updateLogInButton()
        
        Log.d("LogInViewModel", "Password: ${_uiState.value.password} Validity: ${_uiState.value.passwordStatus}")
        
    }
    
    private fun passwordValidation(password: String): PasswordValidationResult {
        return when {
            password.isEmpty() -> PasswordValidationResult.EMPTY
            password.length > MAX_INPUT_LENGTH -> PasswordValidationResult.TOO_LONG
            else -> PasswordValidationResult.VALID
        }
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }
    
    private fun updateLogInButton() {
        _uiState.update { logInUiState ->
            logInUiState.copy(
                isLogInButtonEnabled = !anyEmptyField() &&
                        logInUiState.emailStatus == EmailValidationResult.VALID &&
                        logInUiState.passwordStatus == PasswordValidationResult.VALID
            )
        }
    }
    
    private fun anyEmptyField(): Boolean {
        return _uiState.value.email.isEmpty() || _uiState.value.password.isEmpty()
    }

    fun googleLogin() {
        _uiState.update { logInUiState ->
            logInUiState.copy(
                logInStatus = AccessStatus.LOADING,
                isLogInButtonEnabled = false,
            )
        }

        /*TODO: Retrieve google login data */
        login("admin", "admin")

        when(_uiState.value.logInStatus) {
            AccessStatus.SUCCESS -> { }
            else -> {
                _uiState.update { logInUiState ->
                    logInUiState.copy(
                        logInStatus = AccessStatus.GOOGLE_FAILURE
                    )
                }
            }
        }

    }

    fun inputLogin(){
        login(
            _uiState.value.email,
            _uiState.value.password
        )
    }


    private fun login(
        email: String,
        password: String
    ) {

        idleAccessStatus()

        _uiState.update { logInUiState ->
            logInUiState.copy(
                logInStatus = AccessStatus.LOADING,
                isLogInButtonEnabled = false,
            )
        }

        authenticate(email, password)
        
    }

    fun idleAccessStatus() {
        _uiState.update { logInUiState ->
            logInUiState.copy(
                logInStatus = AccessStatus.IDLE
            )
        }
    }

    private fun authenticate(email: String, password: String) {

        /*TODO: improve authentication*/
     /*   val result = accountRepository.credentialAuth(email, password)

        _uiState.update { logInUiState ->
            when {
                result.success -> {
                    logInUiState.copy(
                        logInStatus = AccessStatus.SUCCESS,
                        authenticationResult = result
                    )
                }
                else -> {
                    logInUiState.copy(
                        logInStatus = AccessStatus.FAILURE,
                        authenticationResult = result
                    )
                }
            }
        }
        */
    }

}