package com.example.chillinapp.ui.access.login

import androidx.lifecycle.ViewModel
import com.example.chillinapp.ui.access.AccessStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LogInViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(LogInUiState())
    val uiState: StateFlow<LogInUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {

        idleAccessStatus()

        _uiState.value = _uiState.value.copy(email = email)

        if (email.isNotEmpty() && _uiState.value.password.isNotEmpty()) {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isLogInButtonEnabled = true,
                    isLogInError = false,
                    logInErrorMessage = ""
                )
            }
        } else {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isLogInButtonEnabled = false,
                    isLogInError = false,
                    logInErrorMessage = ""
                )
            }
        }

    }

    fun updatePassword(password: String) {

        idleAccessStatus()

        _uiState.value = _uiState.value.copy(password = password)

        if (password.isNotEmpty() && _uiState.value.email.isNotEmpty()) {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isLogInButtonEnabled = true,
                    isLogInError = false,
                    logInErrorMessage = ""
                )
            }
        } else {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isLogInButtonEnabled = false,
                    isLogInError = false,
                    logInErrorMessage = ""
                )
            }
        }
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }


    fun googleLogin() {
        _uiState.update { logInUiState ->
            logInUiState.copy(
                isLogInLoading = true,
                isLogInError = false,
                isLogInButtonEnabled = false,
                logInErrorMessage = ""
            )
        }

        /*TODO: Retrieve google login data */
        login("admin", "admin")

        if (_uiState.value.isLogInError) {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    logInErrorMessage = "Google login failed."
                )
            }
        }

    }

    fun inputLogin(){
        login(
            _uiState.value.email,
            _uiState.value.password
        )
        if (_uiState.value.isLogInError) {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    logInErrorMessage = "Invalid email or password."
                )
            }
        }
    }


    private fun login(
        email: String,
        password: String
    ) {

        idleAccessStatus()

        _uiState.update { logInUiState ->
            logInUiState.copy(
                isLogInLoading = true,
                isLogInError = false,
                isLogInButtonEnabled = false,
                logInErrorMessage = ""
            )
        }

        if(authenticate(email, password)) {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isLogInLoading = false,
                    logInStatus = AccessStatus.SUCCESS
                )
            }
        } else {
            _uiState.update { logInUiState ->
                logInUiState.copy(
                    isLogInLoading = false,
                    isLogInError = true,
                    logInStatus = AccessStatus.FAILURE
                )
            }
        }
    }

    fun idleAccessStatus() {
        _uiState.update { logInUiState ->
            logInUiState.copy(
                logInStatus = AccessStatus.IDLE
            )
        }
    }

    private fun authenticate(email: String, password: String): Boolean {
        /*TODO: implement authentication*/
        return email == "admin" && password == "admin"
    }

}