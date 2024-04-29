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

class SignInViewModel(private val accountService: AccountService): ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    companion object{
        private const val MAX_INPUT_LENGTH: Int = 254
        private const val MIN_PSW_LENGTH: Int = 8
    }


    fun updateName(name: String){

        idleResult()

        _uiState.value = _uiState.value.copy(
            account = _uiState.value.account.copy(name = name),
            nameStatus = nameValidation(name),
        )

        updateSignUpButton()

        Log.d("SignInViewModel", "Name: ${_uiState.value.account.name ?: ""} Validity: ${_uiState.value.nameStatus}")

    }

    private fun nameValidation(name: String): NameValidationResult {
        return when {
            name.isEmpty() -> NameValidationResult.EMPTY
            name.length > MAX_INPUT_LENGTH -> NameValidationResult.TOO_LONG
            !name.all { it.isLetter() } -> NameValidationResult.INVALID_CHARACTERS
            else -> NameValidationResult.VALID
        }
    }

    fun updateEmail(email: String) {
        idleResult()

        _uiState.value = _uiState.value.copy(
            account = _uiState.value.account.copy(email = email),
            emailStatus = emailValidation(email),
        )

        updateSignUpButton()

        Log.d("SignInViewModel", "Email: ${_uiState.value.account.email ?: ""} Validity: ${_uiState.value.emailStatus}")

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
            account = _uiState.value.account.copy(password = password),
            passwordStatus = passwordValidation(password),
        )

        updateSignUpButton()

        Log.d("SignInViewModel", "Password: ${_uiState.value.account.password ?: ""} Validity: ${_uiState.value.passwordStatus}")

    }

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


    fun updateConfirmPassword(confirmPassword: String){
        idleResult()

        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordStatus = passwordConfirmValidation(confirmPassword, _uiState.value.account.password ?: ""),
        )

        updateSignUpButton()

        Log.d("SignInViewModel", "Confirm password: ${_uiState.value.confirmPassword} Validity: ${_uiState.value.confirmPasswordStatus}")

    }

    private fun passwordConfirmValidation(confirmPassword: String, password: String): ConfirmPasswordValidationResult {
        return when {
            confirmPassword.isEmpty() -> ConfirmPasswordValidationResult.EMPTY
            confirmPassword != password -> ConfirmPasswordValidationResult.NOT_MATCH
            else -> ConfirmPasswordValidationResult.VALID
        }
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
        Log.d("SignInViewModel", "Password visibility: ${_uiState.value.isPasswordVisible}")
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
        Log.d("SignInViewModel", "Confirm password visibility: ${_uiState.value.isPasswordVisible}")
    }

    private fun updateSignUpButton() {
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

    private fun anyEmptyField(): Boolean {
        return _uiState.value.confirmPassword.isEmpty() ||
                _uiState.value.account.name.isNullOrEmpty() ||
                _uiState.value.account.email.isNullOrEmpty() ||
                _uiState.value.account.password.isNullOrEmpty()
    }


    fun googleSignIn() {
        CoroutineScope(Dispatchers.IO).launch {

            idleResult()

            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isLoading = true,
                    isSignUpButtonEnabled = false,
                )
            }

            /*TODO: Correct google authentication */
            try {
                val result = accountService.googleAuth("idToken")
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

                if(result.success){
                    _uiState.value = SignInUiState(
                        registrationResult = _uiState.value.registrationResult
                    )
                }

            } catch (e: Exception) {
                Log.e("LogInViewModel", "Google Login Error: ", e)
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

            updateSignUpButton()

            Log.d("SignInViewModel", "Google sign in result: ${_uiState.value.registrationResult}")
        }
    }

    fun signIn() {
        CoroutineScope(Dispatchers.IO).launch {

            idleResult()

            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isLoading = true,
                    isSignUpButtonEnabled = false
                )
            }

            try {
                val result = accountService.createAccount(
                    account = _uiState.value.account.copy(
                        password = hashPassword(_uiState.value.account.password!!)
                    )
                )
                _uiState.update { logInUiState ->
                    logInUiState.copy(
                        isLoading = false,
                        registrationResult = result,
                        isSignUpButtonEnabled = !result.success
                    )
                }

                if(result.success){
                    _uiState.value = SignInUiState(
                        registrationResult = result
                    )
                }

            } catch (e: Exception) {
                Log.e("SignInViewModel", "Sign in error: ", e)
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

    fun idleResult() {
        _uiState.update { logInUiState ->
            logInUiState.copy(
                registrationResult = null
            )
        }

        Log.d("SignInViewModel", "Idle result")

    }

}