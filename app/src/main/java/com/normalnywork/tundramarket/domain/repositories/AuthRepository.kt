package com.normalnywork.tundramarket.domain.repositories

import com.normalnywork.tundramarket.domain.entities.UserRole

interface AuthRepository {

    suspend fun authorize(
        phoneNumber: String,
        tradingStationId: Int?,
    )

    suspend fun isLoggedIn(): Boolean

    suspend fun saveIsLoggedIn(isLoggedIn: Boolean)

    suspend fun getUserRole(): UserRole?

    suspend fun saveUserRole(role: UserRole)
}
