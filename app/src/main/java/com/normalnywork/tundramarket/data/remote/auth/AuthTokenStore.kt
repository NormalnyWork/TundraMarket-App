package com.normalnywork.tundramarket.data.remote.auth

interface AuthTokenStore {

    suspend fun getToken(): String?

    suspend fun saveToken(token: String)

    suspend fun clearToken()
}
