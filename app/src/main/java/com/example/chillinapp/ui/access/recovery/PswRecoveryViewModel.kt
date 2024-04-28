package com.example.chillinapp.ui.access.recovery

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PswRecoveryViewModel(private val accountService: AccountService): ViewModel() {

    private val _uiState = MutableStateFlow(PswRecoveryUiState())
    val uiState: StateFlow<PswRecoveryUiState> = _uiState.asStateFlow()

    companion object{
        private const val MAX_INPUT_LENGTH: Int = 254
    }

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

    private fun emailValidation(email: String): EmailValidationResult {
        return when {
            email.isEmpty() -> EmailValidationResult.EMPTY
            email.length > MAX_INPUT_LENGTH -> EmailValidationResult.TOO_LONG
            else -> EmailValidationResult.VALID
        }
    }

    private fun updateButton(){
        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                isButtonEnabled = _uiState.value.emailStatus == EmailValidationResult.VALID
            )
        }
    }

    fun recover(){

        idleResult()

        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                isButtonEnabled = false,
            )
        }

        val result = accountService.recoverPassword(_uiState.value.email)

        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                recoveryResult = result,
                isButtonEnabled = true
            )
        }

    }

    fun idleResult() {
        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                recoveryResult = null
            )
        }
    }

}