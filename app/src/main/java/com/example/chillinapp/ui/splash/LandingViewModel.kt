package com.example.chillinapp.ui.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chillinapp.data.account.AccountService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel class for the Landing screen.
 *
 * This ViewModel manages the UI state for the Landing screen and provides methods to perform actions such as checking
 * if the user is logged in. It uses an AccountService to interact with the account data.
 *
 * @property accountService The AccountService used to interact with the account data.
 */
class LandingViewModel(
    private val accountService: AccountService
): ViewModel(){

    // Mutable state flow for the UI state of the overall screen
    private val _uiState = MutableStateFlow(LandingUiState())

    // State flow for the UI state of the overall screen
    val uiState: StateFlow<LandingUiState> = _uiState.asStateFlow()

    /**
     * Checks if the user is logged in.
     *
     * This method updates the UI state with the result of the login operation. It uses the AccountService to get the
     * current account, and logs the success status of the operation.
     */
    suspend fun isLogged(){

        _uiState.value = LandingUiState(
            login = accountService.getCurrentAccount()
        )
        Log.d("LandingViewModel", "isLogged: ${_uiState.value.login?.success}")

    }

}