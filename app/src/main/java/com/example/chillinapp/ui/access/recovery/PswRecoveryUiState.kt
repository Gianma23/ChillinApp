package com.example.chillinapp.ui.access.recovery

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.AccountErrorType
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult

data class PswRecoveryUiState (
    val email: String = "",

    val emailStatus: EmailValidationResult = EmailValidationResult.IDLE,

    val isButtonEnabled: Boolean = false,

    val isLoading: Boolean = false,
    val recoveryResult: ServiceResult<Unit, AccountErrorType>? = null
)