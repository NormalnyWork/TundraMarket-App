package com.normalnywork.tundramarket.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.normalnywork.tundramarket.domain.entities.TradingStationOrdersPage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

private val IS_ORDER_HISTORY_FULLY_CACHED_KEY = booleanPreferencesKey("is_order_history_initialized")
private val IS_TRADING_STATION_ACTIVE_ORDERS_FULLY_CACHED_KEY =
    booleanPreferencesKey("is_trading_station_active_orders_fully_cached")
private val IS_TRADING_STATION_NEW_ORDERS_FULLY_CACHED_KEY =
    booleanPreferencesKey("is_trading_station_new_orders_fully_cached")
private val IS_TRADING_STATION_HISTORY_ORDERS_FULLY_CACHED_KEY =
    booleanPreferencesKey("is_trading_station_history_orders_fully_cached")
private val TRADING_STATION_ORDERS_LAST_UPDATED_KEY =
    longPreferencesKey("trading_station_orders_last_updated")
private val CURRENT_ORDER_STATUS_LAST_UPDATED_ORDER_ID_KEY =
    longPreferencesKey("current_order_status_last_updated_order_id")
private val CURRENT_ORDER_STATUS_LAST_UPDATED_KEY =
    longPreferencesKey("current_order_status_last_updated")

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

    override suspend fun isTradingStationPageFullyCached(page: TradingStationOrdersPage) =
        context.authPreferencesDataStore.data
            .map { preferences -> preferences[page.fullyCachedKey()] ?: false }
            .first()

    override suspend fun markTradingStationPageFullyCached(page: TradingStationOrdersPage) {
        context.authPreferencesDataStore.edit { preferences ->
            preferences[page.fullyCachedKey()] = true
        }
    }

    override suspend fun areTradingStationPagesFullyCached() =
        TradingStationOrdersPage.entries.all { page ->
            isTradingStationPageFullyCached(page)
        }

    override suspend fun getCurrentOrderStatusLastUpdated(orderId: Int) =
        context.authPreferencesDataStore.data
            .map { preferences ->
                if (preferences[CURRENT_ORDER_STATUS_LAST_UPDATED_ORDER_ID_KEY] == orderId.toLong()) {
                    preferences[CURRENT_ORDER_STATUS_LAST_UPDATED_KEY] ?: 0L
                } else {
                    0L
                }
            }
            .first()

    override suspend fun setCurrentOrderStatusLastUpdated(
        orderId: Int,
        lastUpdated: Long,
    ) {
        context.authPreferencesDataStore.edit { preferences ->
            val storedOrderId = preferences[CURRENT_ORDER_STATUS_LAST_UPDATED_ORDER_ID_KEY]
            val storedLastUpdated = if (storedOrderId == orderId.toLong()) {
                preferences[CURRENT_ORDER_STATUS_LAST_UPDATED_KEY] ?: 0L
            } else {
                0L
            }

            if (lastUpdated > storedLastUpdated) {
                preferences[CURRENT_ORDER_STATUS_LAST_UPDATED_ORDER_ID_KEY] = orderId.toLong()
                preferences[CURRENT_ORDER_STATUS_LAST_UPDATED_KEY] = lastUpdated
            }
        }
    }

    override suspend fun getTradingStationOrdersLastUpdated() =
        context.authPreferencesDataStore.data
            .map { preferences -> preferences[TRADING_STATION_ORDERS_LAST_UPDATED_KEY] ?: 0L }
            .first()

    override suspend fun setTradingStationOrdersLastUpdated(lastUpdated: Long) {
        context.authPreferencesDataStore.edit { preferences ->
            preferences[TRADING_STATION_ORDERS_LAST_UPDATED_KEY] = lastUpdated
        }
    }

    private fun TradingStationOrdersPage.fullyCachedKey() = when (this) {
        TradingStationOrdersPage.Active -> IS_TRADING_STATION_ACTIVE_ORDERS_FULLY_CACHED_KEY
        TradingStationOrdersPage.New -> IS_TRADING_STATION_NEW_ORDERS_FULLY_CACHED_KEY
        TradingStationOrdersPage.History -> IS_TRADING_STATION_HISTORY_ORDERS_FULLY_CACHED_KEY
    }
}
