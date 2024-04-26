package com.example.chillinapp.ui.access.recovery

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chillinapp.ui.access.AccessStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PswRecoveryViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(PswRecoveryUiState())
    val uiState: StateFlow<PswRecoveryUiState> = _uiState.asStateFlow()

    companion object{
        private enum class EmailValidationResult {
            VALID,
            TOO_LONG,
            EMPTY,
            INVALID_FORMAT
        }
        private const val MAX_EMAIL_LENGTH: Int = 200
    }

    fun updateEmail(email: String) {
        idleAccessStatus()

        val validationResult = emailValidation(email)
        when (validationResult) {
            EmailValidationResult.VALID -> _uiState.value = _uiState.value.copy(
                emailErrorMessage = ""
            )
            EmailValidationResult.EMPTY -> _uiState.value = _uiState.value.copy(
                emailErrorMessage = "Email is empty."
            )
            EmailValidationResult.TOO_LONG -> _uiState.value = _uiState.value.copy(
                emailErrorMessage = "Max $MAX_EMAIL_LENGTH characters."
            )
            EmailValidationResult.INVALID_FORMAT -> _uiState.value = _uiState.value.copy(
                emailErrorMessage = "Invalid email format."
            )
        }
        _uiState.value = _uiState.value.copy(
            email = email,
            isEmailValid = validationResult == EmailValidationResult.VALID,
        )
        updateButton()

        Log.d("PswRecoveryViewModel", "Email: ${_uiState.value.email} Validity: ${_uiState.value.isEmailValid}")

    }

    private fun updateButton(){
        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                isButtonEnabled = pswRecoveryUiState.isEmailValid && pswRecoveryUiState.email.isNotEmpty()
            )
        }
    }

    private fun emailValidation(email: String): EmailValidationResult {
        return when {
            email.isEmpty() -> EmailValidationResult.EMPTY
            email.length > MAX_EMAIL_LENGTH -> EmailValidationResult.TOO_LONG
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> EmailValidationResult.INVALID_FORMAT
            else -> EmailValidationResult.VALID
        }
    }

    fun recover(){

        idleAccessStatus()

        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                isRecoveryLoading = true,
                isRecoveryError = false,
                isButtonEnabled = false,
                recoveryErrorMessage = ""
            )
        }

        if(send(_uiState.value.email)) {
            _uiState.update { pswRecoveryUiState ->
                pswRecoveryUiState.copy(
                    isRecoveryLoading = false,
                    recoveryStatus = AccessStatus.SUCCESS
                )
            }
        } else {
            _uiState.update { pswRecoveryUiState ->
                pswRecoveryUiState.copy(
                    isRecoveryLoading = false,
                    isRecoveryError = true,
                    recoveryStatus = AccessStatus.FAILURE
                )
            }
        }

    }

    fun idleAccessStatus() {
        _uiState.update { pswRecoveryUiState ->
            pswRecoveryUiState.copy(
                recoveryStatus = AccessStatus.IDLE
            )
        }

        Log.d("PswRecoveryViewModel", "Snackbar status: ${_uiState.value.recoveryStatus}")

    }

    private fun send(email: String): Boolean {
        /*TODO: implement email recovery*/
        return email == "admin@gmail.com"
    }

}