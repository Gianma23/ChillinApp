package com.example.chillinapp.ui.home.settings

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.AccountErrorType

/**
 * Data class representing the UI state of the settings screen.
 *
 * @property logOutResponse The response from the server when the user tries to log out. It's a ServiceResult object that can either be a success or an error.
 * @property deleteAccountResponse The response from the server when the user tries to delete their account. It's a ServiceResult object that can either be a success or an error.
 * @property name The name of the user.
 * @property email The email of the user.
 * @property loadingOperation A boolean flag indicating whether a network operation is currently being performed.
 * @property isDeleteAccountDialogOpened A boolean flag indicating whether the delete account dialog is currently opened.
 */
data class SettingsUiState (

    val logOutResponse: ServiceResult<Unit, AccountErrorType>? = null,
    val deleteAccountResponse: ServiceResult<Unit, AccountErrorType>? = null,

    val name: String? = null,
    val email: String? = null,

    val loadingOperation: Boolean = false,
    val isDeleteAccountDialogOpened: Boolean = false

)