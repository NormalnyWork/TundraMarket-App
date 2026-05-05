package com.normalnywork.tundramarket.utils

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module

@Module
@ComponentScan("com.normalnywork.tundramarket")
class AppModule

@KoinApplication(modules = [AppModule::class])
class TMKoinApp
