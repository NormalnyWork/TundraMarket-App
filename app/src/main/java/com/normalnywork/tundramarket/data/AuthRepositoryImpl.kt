package com.normalnywork.tundramarket.data

import com.normalnywork.tundramarket.data.local.preferences.UserSessionStore
import com.normalnywork.tundramarket.data.remote.api.auth.AuthTokenStore
import com.normalnywork.tundramarket.data.remote.source.RemoteAuthDataSource
import com.normalnywork.tundramarket.domain.entities.UserRole
import com.normalnywork.tundramarket.domain.repositories.AuthRepository
import org.koin.core.annotation.Singleton

@Singleton
class AuthRepositoryImpl(
    private val remoteAuthDataSource: RemoteAuthDataSource,
    private val authTokenStore: AuthTokenStore,
    private val userSessionStore: UserSessionStore,
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
        userSessionStore.saveIsLoggedIn(true)
        userSessionStore.setTradingStationId(tradingStationId)
    }

    override suspend fun isLoggedIn() = userSessionStore.isLoggedIn()

    /**
     * @return `null` if unauthorized, one of `UserRole` otherwise
     */
    override suspend fun getUserRole() = userSessionStore.getUserRole()

    override suspend fun saveUserRole(role: UserRole) {
        userSessionStore.setUserRole(role)
    }
}
