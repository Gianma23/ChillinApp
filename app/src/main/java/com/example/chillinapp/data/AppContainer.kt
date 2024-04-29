package com.example.chillinapp.data

import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.data.account.FirebaseAccountDao
import com.example.chillinapp.data.account.AccountServiceImpl

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val accountService: AccountService
}

/**
 * [AppContainer] implementation that provides instance of [AccountServiceImpl]
 */
class AppDataContainer() : AppContainer {

    /**
     * Implementation for [AccountService]
     */
    override val accountService: AccountService by lazy {
        AccountServiceImpl(accountDao = FirebaseAccountDao())
    }
}