package com.normalnywork.tundramarket.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.normalnywork.tundramarket.domain.entities.UserRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
private val USER_ROLE_KEY = stringPreferencesKey("user_role")

@Singleton
class DataStoreUserSessionStore(private val context: Context) : UserSessionStore {

    override suspend fun isLoggedIn() = context.authPreferencesDataStore.data
        .map { preferences -> preferences[IS_LOGGED_IN_KEY] ?: false }
        .first()

    override suspend fun saveIsLoggedIn(isLoggedIn: Boolean) {
        context.authPreferencesDataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    override suspend fun getUserRole() = context.authPreferencesDataStore.data
        .map { preferences ->
            preferences[USER_ROLE_KEY]?.let { role ->
                runCatching { UserRole.valueOf(role) }.getOrNull()
            }
        }
        .first()

    override suspend fun setUserRole(role: UserRole) {
        context.authPreferencesDataStore.edit { preferences ->
            preferences[USER_ROLE_KEY] = role.name
        }
    }
}
