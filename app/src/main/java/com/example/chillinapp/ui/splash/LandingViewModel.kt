package com.example.chillinapp.ui.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chillinapp.data.account.AccountService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LandingViewModel(
    private val accountService: AccountService
): ViewModel(){

    // Mutable state flow for the UI state of the overall screen
    private val _uiState = MutableStateFlow(LandingUiState())

    // State flow for the UI state of the overall screen
    val uiState: StateFlow<LandingUiState> = _uiState.asStateFlow()

    suspend fun isLogged(){

        _uiState.value = LandingUiState(
            login = accountService.getCurrentAccount()
        )
        Log.d("LandingViewModel", "isLogged: ${_uiState.value.login?.success}")

    }

}