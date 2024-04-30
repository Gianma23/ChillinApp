package com.example.chillinapp

import android.app.Application
import com.example.chillinapp.data.AppContainer
import com.example.chillinapp.data.AppDataContainer


/**
 * Custom Application class for the ChillIn application.
 *
 * This class extends the Android Application class and is used to maintain global application state. It creates
 * an instance of AppContainer which is used to provide dependencies to the rest of the classes in the application.
 *
 * The AppContainer instance is created in the onCreate method, which is called when the application is starting,
 * before any activity, service, or receiver objects (excluding content providers) have been created.
 */
class ChillInApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies.
     *
     * This property is a lateinit var, meaning that it is guaranteed to be initialized before it is used, and
     * it must be initialized in the onCreate method of the application.
     */
    lateinit var container: AppContainer

    /**
     * Called when the application is starting, before any activity, service, or receiver objects (excluding content providers) have been created.
     *
     * This method initializes the container property with an instance of AppDataContainer.
     */
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer()
    }
}