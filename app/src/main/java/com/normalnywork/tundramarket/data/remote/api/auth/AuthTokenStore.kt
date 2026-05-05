package com.normalnywork.tundramarket.data.remote.api.auth

interface AuthTokenStore {

    suspend fun getToken(): String?

    suspend fun saveToken(token: String)

    suspend fun clearToken()
}
