package com.example.chillinapp.ui.home.settings

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.AccountErrorType

data class SettingsUiState (

    val logOutResponse: ServiceResult<Unit, AccountErrorType>? = null,
    val deleteAccountResponse: ServiceResult<Unit, AccountErrorType>? = null,

    val loadingOperation: Boolean = false,
    val isDeleteAccountDialogOpened: Boolean = false

)