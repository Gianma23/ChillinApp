package com.example.chillinapp.data

import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.data.account.FirebaseAccountService

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val accountService: AccountService
}

/**
 * [AppContainer] implementation that provides instance of [FirebaseAccountService]
 */
class AppDataContainer : AppContainer {

    /**
     * Implementation for [AccountService]
     */
    override val accountService: AccountService by lazy {
        FirebaseAccountService()
    }
}