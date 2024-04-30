package com.example.chillinapp.ui.access.login

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.AccountErrorType
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.PasswordValidationResult


/**
 * Data class representing the UI state for the login screen.
 *
 * @property email The current text in the email field.
 * @property password The current text in the password field.
 * @property emailStatus The validation status of the email field.
 * @property passwordStatus The validation status of the password field.
 * @property isPasswordVisible A flag indicating whether the password is visible or obscured.
 * @property isLogInButtonEnabled A flag indicating whether the login button is enabled.
 * @property isLoading A flag indicating whether a login request is in progress.
 * @property authenticationResult The result of the last login request, if any.
 */
data class LogInUiState (

    val email: String = "",
    val password: String = "",

    val emailStatus: EmailValidationResult = EmailValidationResult.IDLE,
    val passwordStatus: PasswordValidationResult = PasswordValidationResult.IDLE,

    val isPasswordVisible: Boolean = false,

    val isLogInButtonEnabled: Boolean = false,

    val isLoading: Boolean = false,
    val authenticationResult: ServiceResult<Unit, AccountErrorType>? = null

)