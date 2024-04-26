package com.example.chillinapp.ui.access.login

import com.example.chillinapp.ui.access.AccessStatus

data class LogInUiState (
    val email: String = "",
    val password: String = "",

    val isPasswordVisible: Boolean = false,

    val isLogInButtonEnabled: Boolean = false,

    val isLogInLoading: Boolean = false,
    val isLogInError: Boolean = false,
    val logInErrorMessage: String = "",

    val logInStatus: AccessStatus = AccessStatus.IDLE

)