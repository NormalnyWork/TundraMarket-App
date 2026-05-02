package com.normalnywork.tundramarket.data.remote.di

import com.normalnywork.tundramarket.data.remote.auth.AuthTokenStore
import com.normalnywork.tundramarket.data.remote.auth.DataStoreAuthTokenStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val remoteModule = module {
    single<AuthTokenStore> {
        DataStoreAuthTokenStore(context = androidContext())
    }
}
