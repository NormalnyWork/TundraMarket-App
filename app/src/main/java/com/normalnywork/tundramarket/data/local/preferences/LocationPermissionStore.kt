package com.normalnywork.tundramarket.data.local.preferences

import kotlinx.coroutines.flow.Flow

interface LocationPermissionStore {

    fun isAutomaticLocationDetectionForbidden(): Flow<Boolean>

    suspend fun forbidAutomaticLocationDetection()
}
