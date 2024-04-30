package com.example.chillinapp.ui.access.login

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.AccountErrorType
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.PasswordValidationResult

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