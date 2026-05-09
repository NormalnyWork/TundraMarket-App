package com.normalnywork.tundramarket.data.remote.api.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.normalnywork.tundramarket.data.local.preferences.authPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")

@Singleton
class DataStoreAuthTokenStore(private val context: Context) : AuthTokenStore {

    override suspend fun getToken() = context.authPreferencesDataStore.data
        .map { preferences -> preferences[AUTH_TOKEN_KEY] }
        .first()

    override suspend fun saveToken(token: String) {
        context.authPreferencesDataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    override suspend fun clearToken() {
        context.authPreferencesDataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
    }
}
