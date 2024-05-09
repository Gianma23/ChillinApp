package com.example.chillinapp.ui.splash

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.Account
import com.example.chillinapp.data.account.AccountErrorType

/**
 * Data class representing the UI state of the landing screen.
 *
 * This class holds the state of the login operation, which is represented as a ServiceResult. The ServiceResult
 * contains the logged in [Account] (if the login was successful) or an [AccountErrorType] (if the login failed).
 *
 * @property login The result of the login operation. It is a ServiceResult that contains the logged in [Account]
 *                 (if the login was successful) or an [AccountErrorType] (if the login failed). It is nullable and
 *                 defaults to null, which represents the initial state (before the login operation has been performed).
 */
data class LandingUiState (
    val login: ServiceResult<Account?, AccountErrorType>? = null
)