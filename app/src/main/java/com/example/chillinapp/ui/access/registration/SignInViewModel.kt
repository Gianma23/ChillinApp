package com.example.chillinapp.ui.access.registration

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chillinapp.ui.access.AccessStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    companion object{
        private enum class NameValidationResult {
            VALID,
            EMPTY,
            TOO_LONG,
            INVALID_CHARACTERS
        }
        private enum class EmailValidationResult {
            VALID,
            TOO_LONG,
            EMPTY,
            INVALID_FORMAT
        }
        private enum class PasswordValidationResult {
            VALID,
            TOO_SHORT,
            TOO_LONG,
            NO_UPPERCASE,
            NO_LOWERCASE,
            NO_DIGITS,
            NO_SPECIAL_CHAR
        }
        private enum class ConfirmPasswordValidationResult {
            VALID,
            EMPTY,
            NOT_MATCH
        }
        private const val MAX_INPUT_LENGTH: Int = 20
        private const val MAX_EMAIL_LENGTH: Int = 200
        private const val MIN_PSW_LENGTH: Int = 8
    }


    fun updateName(name: String){
        idleAccessStatus()

        val validationResult: NameValidationResult = nameValidation(name)
        when (validationResult) {
            NameValidationResult.VALID -> _uiState.value = _uiState.value.copy(
                nameErrorMessage = ""
            )
            NameValidationResult.EMPTY -> _uiState.value = _uiState.value.copy(
                nameErrorMessage = "Name is empty."
            )
            NameValidationResult.TOO_LONG -> _uiState.value = _uiState.value.copy(
                nameErrorMessage = "Max $MAX_INPUT_LENGTH characters."
            )
            NameValidationResult.INVALID_CHARACTERS -> _uiState.value = _uiState.value.copy(
                nameErrorMessage = "Invalid characters."
            )
        }
        _uiState.value = _uiState.value.copy(
            name = name,
            isNameValid = validationResult == NameValidationResult.VALID,
        )
        updateSignUpButton()

        Log.d("SignInViewModel", "Name: ${_uiState.value.name} Validity: ${_uiState.value.isNameValid}")

    }

    fun updateConfirmPassword(confirmPassword: String){
        idleAccessStatus()

        val validationResult: ConfirmPasswordValidationResult =
            passwordConfirmValidation(confirmPassword, _uiState.value.password)
        when (validationResult) {
            ConfirmPasswordValidationResult.VALID -> _uiState.value = _uiState.value.copy(
                confirmPasswordErrorMessage = ""
            )
            ConfirmPasswordValidationResult.EMPTY -> _uiState.value = _uiState.value.copy(
                confirmPasswordErrorMessage = "Confirm password is empty."
            )
            ConfirmPasswordValidationResult.NOT_MATCH -> _uiState.value = _uiState.value.copy(
                confirmPasswordErrorMessage = "Passwords do not match."
            )
        }
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            isConfirmPasswordValid = validationResult == ConfirmPasswordValidationResult.VALID,
        )
        updateSignUpButton()

        Log.d("SignInViewModel", "Confirm password: ${_uiState.value.confirmPassword} Validity: ${_uiState.value.isConfirmPasswordValid}")

    }

    private fun passwordConfirmValidation(confirmPassword: String, password: String): ConfirmPasswordValidationResult {
        return when {
            confirmPassword.isEmpty() -> ConfirmPasswordValidationResult.EMPTY
            confirmPassword != password -> ConfirmPasswordValidationResult.NOT_MATCH
            else -> ConfirmPasswordValidationResult.VALID
        }
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

        val validationResult = emailValidation(email)
        when (validationResult) {
            EmailValidationResult.VALID -> _uiState.value = _uiState.value.copy(
                emailErrorMessage = ""
            )
            EmailValidationResult.EMPTY -> _uiState.value = _uiState.value.copy(
                emailErrorMessage = "Email is empty."
            )
            EmailValidationResult.TOO_LONG -> _uiState.value = _uiState.value.copy(
                emailErrorMessage = "Max $MAX_INPUT_LENGTH characters."
            )
            EmailValidationResult.INVALID_FORMAT -> _uiState.value = _uiState.value.copy(
                emailErrorMessage = "Invalid email format."
            )
        }
        _uiState.value = _uiState.value.copy(
            email = email,
            isEmailValid = validationResult == EmailValidationResult.VALID,
        )
        updateSignUpButton()

        Log.d("SignInViewModel", "Email: ${_uiState.value.email} Validity: ${_uiState.value.isEmailValid}")

    }

    private fun emailValidation(email: String): EmailValidationResult {
        return when {
            email.isEmpty() -> EmailValidationResult.EMPTY
            email.length > MAX_EMAIL_LENGTH -> EmailValidationResult.TOO_LONG
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> EmailValidationResult.INVALID_FORMAT
            else -> EmailValidationResult.VALID
        }
    }


    fun updatePassword(password: String) {
        idleAccessStatus()

        val validationResult = passwordValidation(password)
        when (validationResult) {
            PasswordValidationResult.VALID -> _uiState.value = _uiState.value.copy(
                passwordErrorMessage = ""
            )
            PasswordValidationResult.TOO_SHORT -> _uiState.value = _uiState.value.copy(
                passwordErrorMessage = "Min $MIN_PSW_LENGTH characters."
            )
            PasswordValidationResult.TOO_LONG -> _uiState.value = _uiState.value.copy(
                passwordErrorMessage = "Max $MAX_INPUT_LENGTH characters."
            )
            PasswordValidationResult.NO_UPPERCASE -> _uiState.value = _uiState.value.copy(
                passwordErrorMessage = "It must contain at least one uppercase letter."
            )
            PasswordValidationResult.NO_LOWERCASE -> _uiState.value = _uiState.value.copy(
                passwordErrorMessage = "It must contain at least one lowercase letter."
            )
            PasswordValidationResult.NO_DIGITS -> _uiState.value = _uiState.value.copy(
                passwordErrorMessage = "It must contain at least one digit."
            )
            PasswordValidationResult.NO_SPECIAL_CHAR -> _uiState.value = _uiState.value.copy(
                passwordErrorMessage = "It must contain at least one special character."
            )
        }
        _uiState.value = _uiState.value.copy(
            password = password,
            isPasswordValid = validationResult == PasswordValidationResult.VALID,
        )
        updateSignUpButton()

        Log.d("SignInViewModel", "Password: ${_uiState.value.password} Validity: ${_uiState.value.isPasswordValid}")

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
                isSignUpButtonEnabled =
                _uiState.value.isNameValid
                        && _uiState.value.isConfirmPasswordValid
                        && _uiState.value.isEmailValid
                        && _uiState.value.isPasswordValid && !anyEmptyField()
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
                isSignInLoading = true,
                isSignInError = false,
                isSignUpButtonEnabled = false,
                signInErrorMessage = ""
            )
        }

        /*TODO: Retrieve google signIn data */
        signIn("admin", "admin")

        if (_uiState.value.isSignInError) {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    signInErrorMessage = "Google signIn failed."
                )
            }
        }

    }

    fun inputSignIn(){
        signIn(
            _uiState.value.email,
            _uiState.value.password
        )
        if (_uiState.value.isSignInError) {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    signInErrorMessage = "Invalid email or password"
                )
            }
        }
    }

    private fun signIn(
        email: String,
        password: String
    ) {

        idleAccessStatus()

        _uiState.update { logInUiState ->
            logInUiState.copy(
                isSignInLoading = true,
                isSignInError = false,
                isSignUpButtonEnabled = false,
                signInErrorMessage = ""
            )
        }

        if(authenticate(email, password)) {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isSignInLoading = false,
                    registrationStatus = AccessStatus.SUCCESS
                )
            }
        } else {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isSignInLoading = false,
                    isSignInError = true,
                    registrationStatus = AccessStatus.FAILURE
                )
            }
        }

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

    private fun authenticate(email: String, password: String): Boolean {
        /*TODO: implement authentication*/
        return email == "admin" && password == "admin"
    }


}