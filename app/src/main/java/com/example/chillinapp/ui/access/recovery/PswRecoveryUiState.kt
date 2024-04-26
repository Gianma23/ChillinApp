package com.example.chillinapp.ui.access.recovery

import com.example.chillinapp.ui.access.AccessStatus

data class PswRecoveryUiState (
    val email: String = "",

    val isEmailValid: Boolean = true,
    val emailErrorMessage: String = "",

    val isButtonEnabled: Boolean = false,

    val isRecoveryLoading: Boolean = false,
    val isRecoveryError: Boolean = false,
    val recoveryErrorMessage: String = "",

    val recoveryStatus: AccessStatus = AccessStatus.IDLE
)