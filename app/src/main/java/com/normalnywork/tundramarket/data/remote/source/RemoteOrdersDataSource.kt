package com.normalnywork.tundramarket.data.remote.source

import com.normalnywork.tundramarket.domain.entities.Order

interface RemoteOrdersDataSource {

    suspend fun createOrder(
        order: Order,
        idempotencyKey: String,
    ): Int
}
