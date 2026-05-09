package com.normalnywork.tundramarket.data.local.preferences

import com.normalnywork.tundramarket.domain.entities.UserRole

interface UserSessionStore {

    suspend fun isLoggedIn(): Boolean

    suspend fun saveIsLoggedIn(isLoggedIn: Boolean)

    suspend fun getUserRole(): UserRole?

    suspend fun setUserRole(role: UserRole)
}
