package com.example.chillinapp.ui.access.recovery

import com.example.chillinapp.ui.access.utility.AccessStatus
import com.example.chillinapp.ui.access.utility.EmailValidationResult

data class PswRecoveryUiState (
    val email: String = "",

    val emailStatus: EmailValidationResult = EmailValidationResult.IDLE,

    val isButtonEnabled: Boolean = false,

    val recoveryStatus: AccessStatus = AccessStatus.IDLE
)