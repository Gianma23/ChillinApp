package com.example.chillinapp.data

import com.example.chillinapp.data.account.AccountService
import com.example.chillinapp.data.account.FirebaseAccountDao
import com.example.chillinapp.data.account.FirebaseAccountService
import com.example.chillinapp.data.stress.FirebaseStressDataDao
import com.example.chillinapp.data.stress.FirebaseStressDataService
import com.example.chillinapp.data.stress.StressDataService

/**
 * Interface for the [AppContainer] which is used for dependency injection in the application.
 *
 * This interface defines the properties that any [AppContainer] implementation must provide. In this case, it requires
 * an instance of AccountService.
 */
interface AppContainer {
    val accountService: AccountService
    val stressDataService: StressDataService
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
     * Lazily initialized property that provides an instance of [AccountService].
     *
     * This property is an instance of [FirebaseAccountService], which is created with a FirebaseAccountDao instance.
     * The property is initialized the first time it is accessed and the same instance is returned for all subsequent
     * accesses, making it a singleton in the scope of this class.
     */
    override val accountService: AccountService by lazy {
        FirebaseAccountService(accountDao = FirebaseAccountDao())
    }

    /**
     * Lazily initialized property that provides an instance of [StressDataService].
     *
     * This property is an instance of [FirebaseStressDataService], which is created with a FirebaseStressDataDao instance.
     * The property is initialized the first time it is accessed and the same instance is returned for all subsequent
     * accesses, making it a singleton in the scope of this class.
     */
    override val stressDataService: StressDataService by lazy {
        FirebaseStressDataService(stressDataDao = FirebaseStressDataDao())
    }
}