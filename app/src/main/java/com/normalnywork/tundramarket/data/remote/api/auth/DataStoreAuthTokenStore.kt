package com.normalnywork.tundramarket.data.remote.api.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

private const val AUTH_DATA_STORE_NAME = "auth_preferences"
private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")

@Singleton
class DataStoreAuthTokenStore(private val context: Context) : AuthTokenStore {

    private val Context.dataStore by preferencesDataStore(name = AUTH_DATA_STORE_NAME)

    override suspend fun getToken() = context.dataStore.data
        .map { preferences -> preferences[AUTH_TOKEN_KEY] }
        .first()

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    override suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
    }
}
