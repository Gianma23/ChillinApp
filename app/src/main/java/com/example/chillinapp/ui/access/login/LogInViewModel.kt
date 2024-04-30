package com.example.chillinapp.ui.access.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.ui.access.utility.hashPassword
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.PasswordValidationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * ViewModel for the login screen.
 * It contains the state and the logic for the login process.
 *
 * @property accountService Service for account related operations.
 */
class LogInViewModel(private val accountService: AccountService): ViewModel() {

    // Mutable state flow for the UI state of the login screen
    private val _uiState = MutableStateFlow(LogInUiState())

    // State flow for the UI state of the login screen
    val uiState: StateFlow<LogInUiState> = _uiState.asStateFlow()

    // Constants for the class
    companion object{
        private const val MAX_INPUT_LENGTH: Int = 254
    }

    /**
     * Updates the email in the UI state and validates it.
     *
     * @param email The new email to be validated.
     */
    fun updateEmail(email: String) {

        idleResult()

        _uiState.value = _uiState.value.copy(
            email = email,
            emailStatus = emailValidation(email)
        )

        updateLogInButton()
        
        Log.d("LogInViewModel", "Email: ${_uiState.value.email} Validity: ${_uiState.value.emailStatus}")

    }

    /**
     * Validates the email.
     *
     * @param email The email to be validated.
     * @return The validation result for the email.
     */
    private fun emailValidation(email: String): EmailValidationResult {
        return when {
            email.isEmpty() -> EmailValidationResult.EMPTY
            email.length > MAX_INPUT_LENGTH -> EmailValidationResult.TOO_LONG
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> EmailValidationResult.INVALID_FORMAT
            else -> EmailValidationResult.VALID
        }
    }

    /**
     * Updates the password in the UI state and validates it.
     *
     * @param password The new password to be validated.
     */
    fun updatePassword(password: String) {

        idleResult()

        _uiState.value = _uiState.value.copy(
            password = password,
            passwordStatus = passwordValidation(password)
        )
        
        updateLogInButton()
        
        Log.d("LogInViewModel", "Password: ${_uiState.value.password} Validity: ${_uiState.value.passwordStatus}")
        
    }

    /**
     * Validates the password.
     *
     * @param password The password to be validated.
     * @return The validation result for the password.
     */
    private fun passwordValidation(password: String): PasswordValidationResult {
        return when {
            password.isEmpty() -> PasswordValidationResult.EMPTY
            password.length > MAX_INPUT_LENGTH -> PasswordValidationResult.TOO_LONG
            else -> PasswordValidationResult.VALID
        }
    }

    /**
     * Toggles the visibility of the password in the UI.
     */
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )

        Log.d("LogInViewModel", "Password Visibility: ${_uiState.value.isPasswordVisible}")

    }

    /**
     * Updates the state of the login button in the UI.
     */
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

    /**
     * Checks if any field in the UI is empty.
     *
     * @return True if any field is empty, false otherwise.
     */
    private fun anyEmptyField(): Boolean {
        return _uiState.value.email.isEmpty() || _uiState.value.password.isEmpty()
    }

    /**
     * Performs the login process with Google.
     */
    fun googleLogin() {

        idleResult()

        _uiState.update { logInUiState ->
            logInUiState.copy(
                isLoading = true,
                isLogInButtonEnabled = false
            )
        }

        CoroutineScope(Dispatchers.IO).launch {

            /*TODO: Fix Google Authentication */
            try {
                val result = accountService.googleAuth("idToken")
                _uiState.update { logInUiState ->
                    logInUiState.copy(
                        authenticationResult = ServiceResult(
                            success = result.success,
                            data = null,
                            error = result.error
                        ),
                        isLoading = false,
                    )
                }

                if(result.success){
                    _uiState.value = LogInUiState(
                        authenticationResult = _uiState.value.authenticationResult
                    )
                }

            } catch (e: Exception) {
                Log.e("LogInViewModel", "Google Login Error: ", e)
                _uiState.update { logInUiState ->
                    logInUiState.copy(
                        authenticationResult = ServiceResult(
                            success = false,
                            data = null,
                            error = null
                        ),
                        isLoading = false,
                    )
                }
            }

            updateLogInButton()

            Log.d("LogInViewModel", "Google Login: ${_uiState.value.authenticationResult}")

        }
    }

    /**
     * Performs the login process with the provided email and password.
     */
    fun login() {

        idleResult()

        _uiState.update { logInUiState ->
            logInUiState.copy(
                isLoading = true,
                isLogInButtonEnabled = false,
            )
        }

        CoroutineScope(Dispatchers.IO).launch {

            try {
                val result = accountService.credentialAuth(
                    email = _uiState.value.email,
                    encryptedPsw = hashPassword(_uiState.value.password)
                )

                _uiState.update { logInUiState ->
                    logInUiState.copy(
                        authenticationResult = result,
                        isLogInButtonEnabled = !result.success,
                        isLoading = false,
                    )
                }

                if(result.success){
                    _uiState.value = LogInUiState(
                        authenticationResult = _uiState.value.authenticationResult
                    )
                }

            } catch (e: Exception) {
                Log.e("LogInViewModel", "Login Error: ", e)
                _uiState.update { logInUiState ->
                    logInUiState.copy(
                        authenticationResult = ServiceResult(
                            success = false,
                            data = null,
                            error = null
                        ),
                        isLoading = false,
                        isLogInButtonEnabled = true
                    )
                }
            }

            Log.d("LogInViewModel", "Login: ${_uiState.value.authenticationResult}")
        }
    }

    /**
     * Resets the result of the authentication in the UI state.
     */
    fun idleResult() {
        _uiState.update { logInUiState ->
            logInUiState.copy(
                authenticationResult = null
            )
        }

        Log.d("LogInViewModel", "Idle Result")

    }
}