package com.example.chillinapp.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chillinapp.ChillInApplication
import com.example.chillinapp.ui.access.login.LogInViewModel
import com.example.chillinapp.ui.access.recovery.PswRecoveryViewModel
import com.example.chillinapp.ui.access.registration.SignInViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            // LogInViewModel
            LogInViewModel(accountRepository = chillInApplication().container.accountRepository)
        }
        initializer {
            // SignInViewModel
            SignInViewModel(accountRepository = chillInApplication().container.accountRepository)
        }
        initializer {
            // PswRecoveryScreen
            PswRecoveryViewModel(accountRepository = chillInApplication().container.accountRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [ChillInApplication].
 */
fun CreationExtras.chillInApplication(): ChillInApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ChillInApplication)