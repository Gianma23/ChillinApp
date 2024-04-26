package com.example.chillinapp.ui.access.registration

import com.example.chillinapp.ui.access.AccessStatus


data class SignInUiState (
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val confirmPassword: String = "",

    val isNameValid: Boolean = true,
    val isConfirmPasswordValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,

    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    val isSignUpButtonEnabled: Boolean = false,

    val isSignInLoading: Boolean = false,
    val isSignInError: Boolean = false,

    val nameErrorMessage: String = "",
    val confirmPasswordErrorMessage: String = "",
    val emailErrorMessage: String = "",
    val passwordErrorMessage: String = "",
    val signInErrorMessage: String = "",

    val registrationStatus: AccessStatus = AccessStatus.IDLE
)