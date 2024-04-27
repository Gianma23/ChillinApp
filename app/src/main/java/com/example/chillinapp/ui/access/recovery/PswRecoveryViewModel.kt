package com.example.chillinapp.ui.access.recovery

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chillinapp.ui.access.utility.AccessStatus
import com.example.chillinapp.ui.access.utility.EmailValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PswRecoveryViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(PswRecoveryUiState())
    val uiState: StateFlow<PswRecoveryUiState> = _uiState.asStateFlow()

    companion object{
        private const val MAX_INPUT_LENGTH: Int = 254
    }

    fun updateEmail(email: String) {
        idleAccessStatus()

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
        idleAccessStatus()

        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                recoveryStatus = AccessStatus.LOADING,
                isButtonEnabled = false,
            )
        }

        send(_uiState.value.email)

        Log.d("PswRecoveryViewModel", "Snackbar status: ${_uiState.value.recoveryStatus}")

    }

    fun idleAccessStatus() {
        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                recoveryStatus = AccessStatus.IDLE
            )
        }

        Log.d("PswRecoveryViewModel", "Snackbar status: ${_uiState.value.recoveryStatus}")

    }

    private fun send(email: String) {
        /*TODO: implement recovery*/

        _uiState.update { logInUiState ->
            when {
                email == "admin" -> {
                    logInUiState.copy(
                        recoveryStatus = AccessStatus.SUCCESS
                    )
                }
                else -> {
                    logInUiState.copy(
                        recoveryStatus = AccessStatus.FAILURE
                    )
                }
            }
        }
    }

}