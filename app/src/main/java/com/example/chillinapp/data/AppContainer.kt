package com.example.chillinapp.data

import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.data.account.FirebaseAccountDao
import com.example.chillinapp.data.account.FirebaseAccountService

/**
 * Interface for the [AppContainer] which is used for dependency injection in the application.
 *
 * This interface defines the properties that any [AppContainer] implementation must provide. In this case, it requires
 * an instance of AccountService.
 */
interface AppContainer {
    val accountService: AccountService
}

/**
 * Implementation of the [AppContainer] interface that provides an instance of [FirebaseAccountService].
 *
 * This class implements the AppContainer interface and provides a concrete implementation of the [accountService] property.
 * The [accountService] property is lazily initialized with an instance of [FirebaseAccountService], which is created with
 * a [FirebaseAccountDao] instance.
 */
class AppDataContainer() : AppContainer {

    /**
     * Lazily initialized property that provides an instance of AccountService.
     *
     * This property is an instance of FirebaseAccountService, which is created with a FirebaseAccountDao instance.
     * The property is initialized the first time it is accessed and the same instance is returned for all subsequent
     * accesses, making it a singleton in the scope of this class.
     */
    override val accountService: AccountService by lazy {
        FirebaseAccountService(accountDao = FirebaseAccountDao())
    }
}