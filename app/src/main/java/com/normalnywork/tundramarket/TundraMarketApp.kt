package com.normalnywork.tundramarket

import android.app.Application
import com.normalnywork.tundramarket.utils.NetworkStatusObserver
import com.normalnywork.tundramarket.utils.TMKoinApp
import com.normalnywork.tundramarket.utils.sync.SyncWorkScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.plugin.module.dsl.startKoin

class TundraMarketApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        startKoin<TMKoinApp> {
            printLogger()
            androidContext(applicationContext)
        }

        val koin = GlobalContext.get()
        val syncWorkScheduler = koin.get<SyncWorkScheduler>()

        syncWorkScheduler.scheduleWeeklyCatalogUpdates()
        syncWorkScheduler.schedule(syncCurrentOrderStatus = true)
        applicationScope.launch {
            koin.get<NetworkStatusObserver>()
                .observe()
                .filter { isConnected -> isConnected }
                .collect {
                    syncWorkScheduler.schedule(
                        replace = true,
                        syncCurrentOrderStatus = true,
                    )
                }
        }
    }
}
