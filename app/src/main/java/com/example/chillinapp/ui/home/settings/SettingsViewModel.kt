package com.example.chillinapp.ui.home.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.Account
import com.example.chillinapp.data.account.AccountErrorType
import com.example.chillinapp.data.account.AccountService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings screen.
 *
 * @property accountService Service to handle account related operations.
 */
class SettingsViewModel(
    private val accountService: AccountService
): ViewModel() {

    // Mutable state flow for the UI state of the settings screen
    private val _uiState = MutableStateFlow(SettingsUiState())

    // State flow for the UI state of the settings screen
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /**
     * Handles the logout operation.
     * Updates the UI state before and after the logout operation.
     */
    fun onLogOut() {

        _uiState.update { settingsUiState ->
            settingsUiState.copy(
                loadingOperation = true
            )
        }

        val response: ServiceResult<Unit, AccountErrorType> = accountService.signOut()
        Log.d("SettingsViewModel", "onLogOut: $response")

        _uiState.update { settingsUiState ->
            settingsUiState.copy(
                logOutResponse = response,
                loadingOperation = false
            )
        }

    }

    /**
     * Toggles the visibility of the delete account dialog.
     * Updates the UI state accordingly.
     */
    fun toggleDeleteAccountDialog() {
        _uiState.update { settingsUiState ->
            settingsUiState.copy(
                isDeleteAccountDialogOpened = !settingsUiState.isDeleteAccountDialogOpened,
                loadingOperation = false
            )
        }
    }

    /**
     * Handles the account deletion operation.
     * Updates the UI state before and after the deletion operation.
     */
    fun onConfirmDeleteAccount() {

        _uiState.update { settingsUiState ->
            settingsUiState.copy(
                loadingOperation = true
            )
        }

        viewModelScope.launch(Dispatchers.IO){
            val currentAccountResponse: ServiceResult<Account?, AccountErrorType> = accountService.getCurrentAccount()

            if (currentAccountResponse.success) {
                val response: ServiceResult<Unit, AccountErrorType> = accountService.deleteAccount(
                    email = currentAccountResponse.data?.email ?: ""
                )
                Log.d("SettingsViewModel", "onConfirmDeleteAccount: $response")
                _uiState.update { settingsUiState ->
                    settingsUiState.copy(
                        deleteAccountResponse = response,
                        loadingOperation = false,
                        isDeleteAccountDialogOpened = false
                    )
                }
            }

        }

    }

    /**
     * Resets the UI state for the settings screen.
     */
    fun toggleNotification() {
        _uiState.value = SettingsUiState()
    }

}