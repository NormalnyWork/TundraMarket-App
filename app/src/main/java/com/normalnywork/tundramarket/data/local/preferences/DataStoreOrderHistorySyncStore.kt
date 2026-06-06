package com.normalnywork.tundramarket.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

private val IS_ORDER_HISTORY_FULLY_CACHED_KEY = booleanPreferencesKey("is_order_history_initialized")

@Singleton
class DataStoreOrderHistorySyncStore(private val context: Context) : OrderHistorySyncStore {

    override suspend fun isHistoryFullyCached() = context.authPreferencesDataStore.data
        .map { preferences -> preferences[IS_ORDER_HISTORY_FULLY_CACHED_KEY] ?: false }
        .first()

    override suspend fun markHistoryFullyCached() {
        context.authPreferencesDataStore.edit { preferences ->
            preferences[IS_ORDER_HISTORY_FULLY_CACHED_KEY] = true
        }
    }
}
