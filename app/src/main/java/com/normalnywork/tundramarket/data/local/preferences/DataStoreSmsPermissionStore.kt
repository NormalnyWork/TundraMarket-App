package com.normalnywork.tundramarket.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

private val IS_CREATE_ORDER_VIA_SMS_FORBIDDEN_KEY = booleanPreferencesKey("is_create_order_via_sms_forbidden")

@Singleton
class DataStoreSmsPermissionStore(private val context: Context) : SmsPermissionStore {

    override fun isCreateOrderViaSmsForbidden() = context.authPreferencesDataStore.data
        .map { preferences -> preferences[IS_CREATE_ORDER_VIA_SMS_FORBIDDEN_KEY] ?: false }

    override suspend fun forbidCreateOrderViaSms() {
        context.authPreferencesDataStore.edit { preferences ->
            preferences[IS_CREATE_ORDER_VIA_SMS_FORBIDDEN_KEY] = true
        }
    }
}
