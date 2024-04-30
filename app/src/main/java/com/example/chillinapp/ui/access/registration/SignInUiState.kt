package com.example.chillinapp.ui.access.registration

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.Account
import com.example.chillinapp.data.account.AccountErrorType
import com.example.chillinapp.ui.access.utility.validationResult.ConfirmPasswordValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.NameValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.PasswordValidationResult


data class SignInUiState (

    val account: Account = Account(),
    val confirmPassword: String = "",

    val nameStatus: NameValidationResult = NameValidationResult.IDLE,
    val emailStatus: EmailValidationResult = EmailValidationResult.IDLE,
    val passwordStatus: PasswordValidationResult = PasswordValidationResult.IDLE,
    val confirmPasswordStatus: ConfirmPasswordValidationResult = ConfirmPasswordValidationResult.IDLE,

    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    val isSignUpButtonEnabled: Boolean = false,

    val isLoading: Boolean = false,
    val registrationResult: ServiceResult<Unit, AccountErrorType>? = null

)