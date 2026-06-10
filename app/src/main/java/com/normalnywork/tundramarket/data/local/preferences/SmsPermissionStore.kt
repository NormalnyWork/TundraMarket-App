package com.normalnywork.tundramarket.data.local.preferences

import kotlinx.coroutines.flow.Flow

interface SmsPermissionStore {

    fun isCreateOrderViaSmsForbidden(): Flow<Boolean>

    suspend fun forbidCreateOrderViaSms()
}
