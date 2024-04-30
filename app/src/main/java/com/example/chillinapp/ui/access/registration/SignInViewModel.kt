package com.example.chillinapp.ui.access.registration

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.ui.access.utility.hashPassword
import com.example.chillinapp.ui.access.utility.validationResult.ConfirmPasswordValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.NameValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.PasswordValidationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * ViewModel for the sign-in screen.
 * It handles the UI state and interactions for the sign-in process.
 *
 * @property accountService The service to interact with the account data.
 */
class SignInViewModel(private val accountService: AccountService): ViewModel() {

    // Mutable state flow for the UI state of the sign-in screen
    private val _uiState = MutableStateFlow(SignInUiState())

    // State flow for the UI state of the sign-in screen
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    // Companion object for constants
    companion object{
        private const val MAX_INPUT_LENGTH: Int = 254
        private const val MIN_PSW_LENGTH: Int = 8
    }

    /**
     * Function to update the name input and its validation status.
     *
     * @param name The name input in the sign-in screen.
     */
    fun updateName(name: String){

        // Reset the registration result to idle
        idleResult()

        // Update the UI state with the new name input and its validation status
        _uiState.value = _uiState.value.copy(
            account = _uiState.value.account.copy(name = name),
            nameStatus = nameValidation(name),
        )

        // Update the enabled status of the sign-up button
        updateSignUpButton()

        Log.d("SignInViewModel", "Name: ${_uiState.value.account.name ?: ""} Validity: ${_uiState.value.nameStatus}")

    }

    /**
     * Function to validate the name input.
     *
     * @param name The name input in the sign-in screen.
     * @return The validation result of the name input.
     */
    private fun nameValidation(name: String): NameValidationResult {
        return when {
            name.isEmpty() -> NameValidationResult.EMPTY
            name.length > MAX_INPUT_LENGTH -> NameValidationResult.TOO_LONG
            !name.all { it.isLetter() } -> NameValidationResult.INVALID_CHARACTERS
            else -> NameValidationResult.VALID
        }
    }

    /**
     * Function to update the email input and its validation status.
     *
     * @param email The email input in the sign-in screen.
     */
    fun updateEmail(email: String) {

        // Reset the registration result to idle
        idleResult()

        // Update the UI state with the new email input and its validation status
        _uiState.value = _uiState.value.copy(
            account = _uiState.value.account.copy(email = email),
            emailStatus = emailValidation(email),
        )

        // Update the enabled status of the sign-up button
        updateSignUpButton()

        Log.d("SignInViewModel", "Email: ${_uiState.value.account.email ?: ""} Validity: ${_uiState.value.emailStatus}")

    }

    /**
     * Function to validate the email input.
     *
     * @param email The email input in the sign-in screen.
     * @return The validation result of the email input.
     */
    private fun emailValidation(email: String): EmailValidationResult {
        return when {
            email.isEmpty() -> EmailValidationResult.EMPTY
            email.length > MAX_INPUT_LENGTH -> EmailValidationResult.TOO_LONG
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> EmailValidationResult.INVALID_FORMAT
            /*TODO: Implement in-use validation */
            else -> EmailValidationResult.VALID
        }
    }

    /**
     * Function to update the password input and its validation status.
     *
     * @param password The password input in the sign-in screen.
     */
    fun updatePassword(password: String) {

        // Reset the registration result to idle
        idleResult()

        // Update the UI state with the new password input and its validation status
        _uiState.value = _uiState.value.copy(
            account = _uiState.value.account.copy(password = password),
            passwordStatus = passwordValidation(password),
        )

        // Update the enabled status of the sign-up button
        updateSignUpButton()

        Log.d("SignInViewModel", "Password: ${_uiState.value.account.password ?: ""} Validity: ${_uiState.value.passwordStatus}")

    }

    /**
     * Function to validate the password input.
     *
     * @param password The password input in the sign-in screen.
     * @return The validation result of the password input.
     */
    private fun passwordValidation(password: String): PasswordValidationResult {
        return when {
            password.length < MIN_PSW_LENGTH -> PasswordValidationResult.TOO_SHORT
            password.length > MAX_INPUT_LENGTH -> PasswordValidationResult.TOO_LONG
            !password.any { it.isUpperCase() } -> PasswordValidationResult.NO_UPPERCASE
            !password.any { it.isLowerCase() } -> PasswordValidationResult.NO_LOWERCASE
            !password.any { it.isDigit() } -> PasswordValidationResult.NO_DIGITS
            !password.any { !it.isLetterOrDigit() } -> PasswordValidationResult.NO_SPECIAL_CHAR
            else -> PasswordValidationResult.VALID
        }
    }

    /**
     * Function to update the confirm password input and its validation status.
     *
     * @param confirmPassword The confirm password input in the sign-in screen.
     */
    fun updateConfirmPassword(confirmPassword: String){

        // Reset the registration result to idle
        idleResult()

        // Update the UI state with the new confirm password input and its validation status
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordStatus = passwordConfirmValidation(confirmPassword, _uiState.value.account.password ?: ""),
        )

        // Update the enabled status of the sign-up button
        updateSignUpButton()

        Log.d("SignInViewModel", "Confirm password: ${_uiState.value.confirmPassword} Validity: ${_uiState.value.confirmPasswordStatus}")

    }

    /**
     * Function to validate the confirm password input.
     *
     * @param confirmPassword The confirm password input in the sign-in screen.
     * @param password The password input in the sign-in screen.
     * @return The validation result of the confirm password input.
     */
    private fun passwordConfirmValidation(confirmPassword: String, password: String): ConfirmPasswordValidationResult {
        return when {
            confirmPassword.isEmpty() -> ConfirmPasswordValidationResult.EMPTY
            confirmPassword != password -> ConfirmPasswordValidationResult.NOT_MATCH
            else -> ConfirmPasswordValidationResult.VALID
        }
    }

    /**
     * Function to toggle the visibility of the password input.
     */
    fun togglePasswordVisibility() {
        // Update the UI state with the new visibility status of the password input
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
        Log.d("SignInViewModel", "Password visibility: ${_uiState.value.isPasswordVisible}")
    }

    /**
     * Function to toggle the visibility of the confirm password input.
     */
    fun toggleConfirmPasswordVisibility() {
        // Update the UI state with the new visibility status of the confirm password input
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
        Log.d("SignInViewModel", "Confirm password visibility: ${_uiState.value.isPasswordVisible}")
    }

    /**
     * Function to update the enabled status of the sign-up button.
     */
    private fun updateSignUpButton() {
        // Update the UI state with the new enabled status of the sign-up button
        _uiState.update { logInUiState ->
            logInUiState.copy(
                isSignUpButtonEnabled = !anyEmptyField() &&
                        logInUiState.nameStatus == NameValidationResult.VALID &&
                        logInUiState.emailStatus == EmailValidationResult.VALID &&
                        logInUiState.passwordStatus == PasswordValidationResult.VALID &&
                        logInUiState.confirmPasswordStatus == ConfirmPasswordValidationResult.VALID
            )
        }

        Log.d("SignInViewModel", "Sign up button enabled: ${_uiState.value.isSignUpButtonEnabled}")
    }

    /**
     * Function to check if any field is empty.
     *
     * @return True if any field is empty, false otherwise.
     */
    private fun anyEmptyField(): Boolean {
        return _uiState.value.confirmPassword.isEmpty() ||
                _uiState.value.account.name.isNullOrEmpty() ||
                _uiState.value.account.email.isNullOrEmpty() ||
                _uiState.value.account.password.isNullOrEmpty()
    }

    /**
     * Function to handle the Google sign-in process.
     */
    fun googleSignIn() {

        // Launch a coroutine in the IO dispatcher
        CoroutineScope(Dispatchers.IO).launch {

            // Reset the registration result to idle
            idleResult()

            // Update the UI state with the loading status and the disabled status of the sign-up button
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isLoading = true,
                    isSignUpButtonEnabled = false,
                )
            }

            /*TODO: Correct google authentication */
            try {
                // Attempt to authenticate with Google
                val result = accountService.googleAuth("idToken")

                // Update the UI state with the result of the Google authentication
                _uiState.update { logInUiState ->
                    logInUiState.copy(
                        registrationResult = ServiceResult(
                            success = result.success,
                            data = null,
                            error = result.error
                        ),
                        isLoading = false,
                    )
                }

                // If the Google authentication is successful, reset the UI state with the registration result
                if(result.success){
                    _uiState.value = SignInUiState(
                        registrationResult = _uiState.value.registrationResult
                    )
                }

            } catch (e: Exception) {
                Log.e("LogInViewModel", "Google Login Error: ", e)

                // If an exception occurs, update the UI state with the failed registration result
                _uiState.update { logInUiState ->
                    logInUiState.copy(
                        registrationResult = ServiceResult(
                            success = false,
                            data = null,
                            error = null
                        ),
                        isLoading = false,
                    )
                }
            }

            // Update the enabled status of the sign-up button
            updateSignUpButton()

            Log.d("SignInViewModel", "Google sign in result: ${_uiState.value.registrationResult}")
        }
    }

    /**
     * Function to handle the sign-in process.
     */
    fun signIn() {

        // Launch a coroutine in the IO dispatcher
        CoroutineScope(Dispatchers.IO).launch {

            // Reset the registration result to idle
            idleResult()

            // Update the UI state with the loading status and the disabled status of the sign-up button
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isLoading = true,
                    isSignUpButtonEnabled = false
                )
            }

            try {

                // Attempt to create an account with the account data
                val result = accountService.createAccount(
                    account = _uiState.value.account.copy(
                        password = hashPassword(_uiState.value.account.password!!)
                    )
                )

                // Update the UI state with the result of the registration
                _uiState.update { logInUiState ->
                    logInUiState.copy(
                        isLoading = false,
                        registrationResult = result,
                        isSignUpButtonEnabled = !result.success
                    )
                }

                // If the registration is successful, reset the UI state with the registration result
                if(result.success){
                    _uiState.value = SignInUiState(
                        registrationResult = result
                    )
                }

            } catch (e: Exception) {
                Log.e("SignInViewModel", "Sign in error: ", e)

                // If an exception occurs, update the UI state with the failed registration result
                _uiState.update { logInUiState ->
                    logInUiState.copy(
                        registrationResult = ServiceResult(
                            success = false,
                            data = null,
                            error = null
                        ),
                        isLoading = false,
                        isSignUpButtonEnabled = true
                    )
                }
            }
            Log.d("SignInViewModel", "Sign in result: ${_uiState.value.registrationResult}")
        }
    }

    /**
     * Function to reset the registration result to idle.
     */
    fun idleResult() {
        // Update the UI state with the idle registration result
        _uiState.update { logInUiState ->
            logInUiState.copy(
                registrationResult = null
            )
        }

        Log.d("SignInViewModel", "Idle result")

    }

}