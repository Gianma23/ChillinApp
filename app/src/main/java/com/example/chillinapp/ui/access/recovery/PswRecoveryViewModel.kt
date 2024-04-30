package com.example.chillinapp.ui.access.recovery

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * ViewModel for the password recovery screen.
 * It handles the UI state and interactions for the password recovery process.
 *
 * @property accountService The service to interact with the account data.
 */
class PswRecoveryViewModel(private val accountService: AccountService): ViewModel() {

    // Mutable state flow for the UI state of the password recovery screen
    private val _uiState = MutableStateFlow(PswRecoveryUiState())

    // State flow for the UI state of the password recovery screen
    val uiState: StateFlow<PswRecoveryUiState> = _uiState.asStateFlow()

    // Companion object for constants
    companion object{
        private const val MAX_INPUT_LENGTH: Int = 254
    }

    /**
     * Function to update the email input and its validation status.
     *
     * @param email The email input in the password recovery screen.
     */
    fun updateEmail(email: String) {
        idleResult()

        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                email = email,
                emailStatus = emailValidation(email),
            )
        }

        updateButton()

        Log.d("PswRecoveryViewModel", "Email: ${_uiState.value.email} Validity: ${_uiState.value.emailStatus}")

    }

    /**
     * Function to validate the email input.
     *
     * @param email The email input in the password recovery screen.
     * @return The validation result of the email input.
     */
    private fun emailValidation(email: String): EmailValidationResult {
        return when {
            email.isEmpty() -> EmailValidationResult.EMPTY
            email.length > MAX_INPUT_LENGTH -> EmailValidationResult.TOO_LONG
            else -> EmailValidationResult.VALID
        }
    }

    /**
     * Function to update the enabled status of the password recovery button.
     */
    private fun updateButton(){
        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                isButtonEnabled = _uiState.value.emailStatus == EmailValidationResult.VALID
            )
        }

        Log.d("PswRecoveryViewModel", "Button Enabled: ${_uiState.value.isButtonEnabled}")

    }

    /**
     * Function to handle the password recovery process.
     */
    fun recover(){

        idleResult()

        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                isLoading = true,
                isButtonEnabled = false,
            )
        }

        CoroutineScope(Dispatchers.IO).launch {

            try {
                val result = accountService.recoverPassword(_uiState.value.email)
                _uiState.update { pswRecoveryUiState ->
                    pswRecoveryUiState.copy(
                        isLoading = false,
                        recoveryResult = result,
                        isButtonEnabled = !result.success
                    )
                }

                if (result.success) {
                    _uiState.value = PswRecoveryUiState(
                        recoveryResult = result,
                    )
                }

            } catch (e: Exception) {
                _uiState.update { pswRecoveryUiState ->
                    pswRecoveryUiState.copy(
                        isLoading = false,
                        recoveryResult = null,
                        isButtonEnabled = true
                    )
                }
            }

            Log.d("PswRecoveryViewModel", "Recovery Result: ${_uiState.value.recoveryResult}")
        }

    }

    /**
     * Function to reset the recovery result to idle.
     */
    fun idleResult() {
        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                recoveryResult = null
            )
        }

        Log.d("PswRecoveryViewModel", "Idle Result")

    }

}