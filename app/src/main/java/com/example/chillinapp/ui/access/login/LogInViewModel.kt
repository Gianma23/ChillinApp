package com.example.chillinapp.ui.access.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.ui.access.utility.hashPassword
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.PasswordValidationResult
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

        idleResult()

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

        idleResult()

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

        Log.d("LogInViewModel", "Password Visibility: ${_uiState.value.isPasswordVisible}")

    }
    
    private fun updateLogInButton() {
        _uiState.update { logInUiState ->
            logInUiState.copy(
                isLogInButtonEnabled = !anyEmptyField() &&
                        logInUiState.emailStatus == EmailValidationResult.VALID &&
                        logInUiState.passwordStatus == PasswordValidationResult.VALID
            )
        }

        Log.d("LogInViewModel", "LogIn Button Enabled: ${_uiState.value.isLogInButtonEnabled}")

    }
    
    private fun anyEmptyField(): Boolean {
        return _uiState.value.email.isEmpty() || _uiState.value.password.isEmpty()
    }

    fun googleLogin() {

        idleResult()

        _uiState.update { logInUiState ->
            logInUiState.copy(
                isLogInButtonEnabled = false,
            )
        }

        val result = accountService.googleAuth()

        _uiState.update { logInUiState ->
            logInUiState.copy(
                authenticationResult = result
            )
        }
        updateLogInButton()

        Log.d("LogInViewModel", "Google Login: ${_uiState.value.authenticationResult}")

    }

    fun login() {

        idleResult()

        _uiState.update { logInUiState ->
            logInUiState.copy(
                isLogInButtonEnabled = false,
            )
        }

        val result = accountService.credentialAuth(
            email = _uiState.value.email,
            encryptedPsw = hashPassword(_uiState.value.password)
        )

        _uiState.update { logInUiState ->
            logInUiState.copy(
                authenticationResult = result,
                isLogInButtonEnabled = !result.success
            )
        }

        Log.d("LogInViewModel", "Login: ${_uiState.value.authenticationResult}")
        
    }


    fun idleResult() {
        _uiState.update { logInUiState ->
            logInUiState.copy(
                authenticationResult = null
            )
        }

        Log.d("LogInViewModel", "Idle Result")

    }
}