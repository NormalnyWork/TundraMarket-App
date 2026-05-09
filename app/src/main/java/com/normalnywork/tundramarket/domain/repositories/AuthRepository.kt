package com.normalnywork.tundramarket.domain.repositories

import com.normalnywork.tundramarket.domain.entities.UserRole

interface AuthRepository {

    suspend fun authorize(
        phoneNumber: String,
        tradingStationId: Int?,
    )

    suspend fun isLoggedIn(): Boolean

    /**
    * @return `null` if unauthorized, one of `UserRole` otherwise
    */
    suspend fun getUserRole(): UserRole?

    suspend fun saveUserRole(role: UserRole)
}
