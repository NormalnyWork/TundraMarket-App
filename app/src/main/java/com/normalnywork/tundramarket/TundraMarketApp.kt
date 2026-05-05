package com.normalnywork.tundramarket

import android.app.Application
import com.normalnywork.tundramarket.utils.TMKoinApp
import org.koin.android.ext.koin.androidContext
import org.koin.plugin.module.dsl.startKoin

class TundraMarketApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin<TMKoinApp> {
            printLogger()
            androidContext(applicationContext)
        }
    }
}