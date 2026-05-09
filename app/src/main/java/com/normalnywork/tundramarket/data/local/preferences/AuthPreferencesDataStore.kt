package com.normalnywork.tundramarket.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private const val AUTH_DATA_STORE_NAME = "auth_preferences"

val Context.authPreferencesDataStore by preferencesDataStore(name = AUTH_DATA_STORE_NAME)
