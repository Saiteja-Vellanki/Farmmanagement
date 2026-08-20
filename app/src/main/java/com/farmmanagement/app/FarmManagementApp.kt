package com.farmmanagement.app

import android.app.Application
import com.farmmanagement.app.di.AppContainer

class FarmManagementApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
