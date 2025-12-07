package com.example.wheretotravel

import android.app.Application
import com.example.wheretotravel.di.AppContainer

class WhereToTravelApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}