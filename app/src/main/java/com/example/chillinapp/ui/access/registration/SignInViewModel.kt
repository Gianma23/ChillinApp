package com.example.chillinapp.ui.access.registration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chillinapp.data.account.Account
import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.ui.access.utility.AccessStatus
import com.example.chillinapp.ui.access.utility.ConfirmPasswordValidationResult
import com.example.chillinapp.ui.access.utility.EmailValidationResult
import com.example.chillinapp.ui.access.utility.NameValidationResult
import com.example.chillinapp.ui.access.utility.PasswordValidationResult
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

        idleAccessStatus()

        _uiState.value = _uiState.value.copy(
            name = name,
            nameStatus = nameValidation(name),
        )

        updateSignUpButton()

        Log.d("SignInViewModel", "Name: ${_uiState.value.name} Validity: ${_uiState.value.nameStatus}")

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
        idleAccessStatus()

        _uiState.value = _uiState.value.copy(
            email = email,
            emailStatus = emailValidation(email),
        )

        updateSignUpButton()

        Log.d("SignInViewModel", "Email: ${_uiState.value.email} Validity: ${_uiState.value.emailStatus}")

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
            passwordStatus = passwordValidation(password),
        )

        updateSignUpButton()

        Log.d("SignInViewModel", "Password: ${_uiState.value.password} Validity: ${_uiState.value.passwordStatus}")

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
        idleAccessStatus()

        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordStatus = passwordConfirmValidation(confirmPassword, _uiState.value.password),
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
        return _uiState.value.name.isEmpty() ||
                _uiState.value.confirmPassword.isEmpty() ||
                _uiState.value.email.isEmpty() ||
                _uiState.value.password.isEmpty()
    }


    fun googleSignIn() {
        _uiState.update { logInUiState ->
            logInUiState.copy(
                registrationStatus = AccessStatus.LOADING,
                isSignUpButtonEnabled = false,
            )
        }

        /*TODO: Retrieve google sign in data */
        signIn("admin", "admin")

        if (_uiState.value.registrationStatus != AccessStatus.SUCCESS) {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    registrationStatus = AccessStatus.GOOGLE_FAILURE
                )
            }
        }

    }

    fun inputSignIn(){
        signIn(
            _uiState.value.email,
            _uiState.value.password
        )
        val email = _uiState.value.email
        val password = _uiState.value.password
        val name=_uiState.value.name
        viewModelScope.launch {
            val sucess=accountService.createAccount(Account(name,email,password))
            if(sucess){
                Log.d("FROM MODEL TO REPO", "SUCCESS")
            }
            else
                Log.d("FROM MODEL TO REPO", "NO GOOD")

        }

    }


    private fun signIn(
        email: String,
        password: String
    ) {
        idleAccessStatus()

        _uiState.update { logInUiState ->
            logInUiState.copy(
                registrationStatus = AccessStatus.LOADING,
                isSignUpButtonEnabled = false
            )
        }

        authenticate(email, password)

        Log.d("SignInViewModel", "Sign in: ${_uiState.value.registrationStatus}")

    }

    fun idleAccessStatus() {
        _uiState.update { logInUiState ->
            logInUiState.copy(
                registrationStatus = AccessStatus.IDLE
            )
        }

        Log.d("SignInViewModel", "Snackbar status: ${_uiState.value.registrationStatus}")

    }

    private fun authenticate(email: String, password: String) {
        /*TODO: implement authentication*/
        _uiState.update { logInUiState ->
            when {
                email == "admin" && password == "admin" -> {
                    logInUiState.copy(
                        registrationStatus = AccessStatus.SUCCESS
                    )
                }
                else -> {
                    logInUiState.copy(
                        registrationStatus = AccessStatus.FAILURE
                    )
                }
            }
        }
    }


}