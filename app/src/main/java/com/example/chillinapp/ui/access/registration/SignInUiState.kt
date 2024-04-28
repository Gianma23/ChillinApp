package com.example.chillinapp.ui.access.registration

import com.example.chillinapp.ui.access.utility.AccessStatus
import com.example.chillinapp.ui.access.utility.ConfirmPasswordValidationResult
import com.example.chillinapp.ui.access.utility.EmailValidationResult
import com.example.chillinapp.ui.access.utility.NameValidationResult
import com.example.chillinapp.ui.access.utility.PasswordValidationResult


data class SignInUiState (

    val email: String = "",
    val password: String = "",
    val name: String = "",
    val confirmPassword: String = "",

    val nameStatus: NameValidationResult = NameValidationResult.IDLE,
    val emailStatus: EmailValidationResult = EmailValidationResult.IDLE,
    val passwordStatus: PasswordValidationResult = PasswordValidationResult.IDLE,
    val confirmPasswordStatus: ConfirmPasswordValidationResult = ConfirmPasswordValidationResult.IDLE,

    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    val isSignUpButtonEnabled: Boolean = false,

    val registrationStatus: AccessStatus = AccessStatus.IDLE

)