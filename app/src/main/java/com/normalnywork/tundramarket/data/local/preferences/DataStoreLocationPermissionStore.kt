package com.normalnywork.tundramarket.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

private val IS_AUTOMATIC_LOCATION_DETECTION_FORBIDDEN_KEY =
    booleanPreferencesKey("is_automatic_location_detection_forbidden")

@Singleton
class DataStoreLocationPermissionStore(private val context: Context) : LocationPermissionStore {

    override fun isAutomaticLocationDetectionForbidden() = context.authPreferencesDataStore.data
        .map { preferences -> preferences[IS_AUTOMATIC_LOCATION_DETECTION_FORBIDDEN_KEY] ?: false }

    override suspend fun forbidAutomaticLocationDetection() {
        context.authPreferencesDataStore.edit { preferences ->
            preferences[IS_AUTOMATIC_LOCATION_DETECTION_FORBIDDEN_KEY] = true
        }
    }
}
