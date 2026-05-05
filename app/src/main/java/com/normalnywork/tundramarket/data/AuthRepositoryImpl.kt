package com.normalnywork.tundramarket.data

import com.normalnywork.tundramarket.data.remote.api.auth.AuthTokenStore
import com.normalnywork.tundramarket.data.remote.source.RemoteAuthDataSource
import com.normalnywork.tundramarket.domain.repositories.AuthRepository
import org.koin.core.annotation.Singleton

@Singleton
class AuthRepositoryImpl(
    private val remoteAuthDataSource: RemoteAuthDataSource,
    private val authTokenStore: AuthTokenStore,
) : AuthRepository {

    override suspend fun authorize(
        phoneNumber: String,
        tradingStationId: Int?,
    ) {
        val token = remoteAuthDataSource.authorize(
            phoneNumber = phoneNumber,
            tradingStationId = tradingStationId,
        )
        authTokenStore.saveToken(token)
    }
}
