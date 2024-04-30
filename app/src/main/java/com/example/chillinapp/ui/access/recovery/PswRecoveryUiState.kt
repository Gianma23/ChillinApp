package com.example.chillinapp.ui.access.recovery

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.AccountErrorType
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult


/**
 * Data class representing the UI state for the password recovery screen.
 * It contains the email input, email validation status, button enabled status, loading status, and recovery result.
 *
 * @property email The email input in the password recovery screen.
 * @property emailStatus The validation status of the email input.
 * @property isButtonEnabled The enabled status of the password recovery button.
 * @property isLoading The loading status of the password recovery process.
 * @property recoveryResult The result of the password recovery process.
 */
data class PswRecoveryUiState (
    val email: String = "",

    val emailStatus: EmailValidationResult = EmailValidationResult.IDLE,

    val isButtonEnabled: Boolean = false,

    val isLoading: Boolean = false,
    val recoveryResult: ServiceResult<Unit, AccountErrorType>? = null
)