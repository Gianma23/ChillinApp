package com.example.chillinapp

import android.app.Application
import com.example.chillinapp.data.AppContainer
import com.example.chillinapp.data.AppDataContainer

class ChillInApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer()
    }
}