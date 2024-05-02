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
import com.example.chillinapp.ui.home.map.MapViewModel
import com.example.chillinapp.ui.home.monitor.MonitorViewModel
import com.example.chillinapp.ui.home.settings.SettingsViewModel


/**
 * Object that provides a factory to create instances of ViewModel for the entire ChillIn app.
 *
 * This object uses the viewModelFactory function from the androidx.lifecycle.viewmodel library to create
 * initializers for each ViewModel in the application. Each initializer creates an instance of a ViewModel
 * with the accountService from the ChillInApplication's container.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            // LogInViewModel
            LogInViewModel(accountService = chillInApplication().container.accountService)
        }
        initializer {
            // SignInViewModel
            SignInViewModel(accountService = chillInApplication().container.accountService)
        }
        initializer {
            // PswRecoveryScreen
            PswRecoveryViewModel(accountService = chillInApplication().container.accountService)
        }
        initializer {
            // MonitorViewModel
            MonitorViewModel(
//                dataService = chillInApplication().container.stressDataService
            )
        }
        initializer {
            // MapViewModel
            MapViewModel(dataService = chillInApplication().container.stressDataService)
        }
        initializer {
            SettingsViewModel()
        }
    }
}

/**
 * Extension function to query for [Application] object and return an instance of
 * [ChillInApplication].
 *
 * This function uses the AndroidViewModelFactory.APPLICATION_KEY to retrieve the Application object from
 * the CreationExtras. It then casts this object to a ChillInApplication and returns it.
 *
 * @return The Application object cast to a ChillInApplication.
 */
fun CreationExtras.chillInApplication(): ChillInApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ChillInApplication)