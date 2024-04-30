package com.example.chillinapp.data

import com.example.chillinapp.data.stress.FirebaseStressDataDao
import com.example.chillinapp.data.stress.FirebaseStressDataService
import com.example.chillinapp.data.stress.StressDataService
import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.data.account.FirebaseAccountDao
import com.example.chillinapp.data.account.FirebaseAccountService

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val accountService: AccountService
    val stressDataService: StressDataService
}

/**
 * [AppContainer] implementation that provides instance of [FirebaseAccountService]
 */
class AppDataContainer() : AppContainer {

    /**
     * Implementation for [AccountService]
     */
    override val accountService: AccountService by lazy {
        FirebaseAccountService(accountDao = FirebaseAccountDao())
    }

    override val stressDataService: StressDataService by lazy {
        FirebaseStressDataService(stressDataDao = FirebaseStressDataDao())
    }
}