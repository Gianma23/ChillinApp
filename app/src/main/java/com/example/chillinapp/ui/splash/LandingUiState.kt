package com.example.chillinapp.ui.splash

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.Account
import com.example.chillinapp.data.account.AccountErrorType

data class LandingUiState (
    val login: ServiceResult<Account?, AccountErrorType>? = null
)