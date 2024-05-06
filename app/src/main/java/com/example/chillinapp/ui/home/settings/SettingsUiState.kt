package com.example.chillinapp.ui.home.settings

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.AccountErrorType

/**
 * Data class that represents the UI state of the settings screen.
 *
 * @param logOutResponse The response of the log out operation.
 * @param deleteAccountResponse The response of the delete account operation.
 * @param loadingOperation The loading state of the operation.
 * @param isDeleteAccountDialogOpened The state of the delete account dialog.
 */
data class SettingsUiState (

    val logOutResponse: ServiceResult<Unit, AccountErrorType>? = null,
    val deleteAccountResponse: ServiceResult<Unit, AccountErrorType>? = null,

    val loadingOperation: Boolean = false,
    val isDeleteAccountDialogOpened: Boolean = false

)