package com.example.chillinapp.data

import com.example.chillinapp.data.account.AccountRepository
import com.example.chillinapp.data.account.FirebaseAccountRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val accountRepository: AccountRepository
}

/**
 * [AppContainer] implementation that provides instance of [FirebaseAccountRepository]
 */
class AppDataContainer() : AppContainer {
    /**
     * Implementation for [AccountRepository]
     */
    override val accountRepository: AccountRepository by lazy {
        FirebaseAccountRepository()
    }
}