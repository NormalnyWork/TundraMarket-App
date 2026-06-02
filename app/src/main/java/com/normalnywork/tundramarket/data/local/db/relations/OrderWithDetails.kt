package com.normalnywork.tundramarket.data.local.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_COL_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_COL_TRADING_STATION_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_PRODUCT_COL_ORDER_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_STATUS_HISTORY_COL_ORDER_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_TRADING_STATION_COL_ID
import com.normalnywork.tundramarket.data.local.db.entities.OrderEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderProductEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusHistoryEntity
import com.normalnywork.tundramarket.data.local.db.entities.TradingStationEntity

data class OrderWithDetails(
    @Embedded
    val order: OrderEntity,
    @Relation(
        parentColumn = DB_ORDER_COL_TRADING_STATION_ID,
        entityColumn = DB_TRADING_STATION_COL_ID,
    )
    val tradingStation: TradingStationEntity,
    @Relation(
        entity = OrderProductEntity::class,
        parentColumn = DB_ORDER_COL_ID,
        entityColumn = DB_ORDER_PRODUCT_COL_ORDER_ID,
    )
    val cart: List<OrderProductWithProduct>,
    @Relation(
        parentColumn = DB_ORDER_COL_ID,
        entityColumn = DB_ORDER_STATUS_HISTORY_COL_ORDER_ID,
    )
    val statusHistory: List<OrderStatusHistoryEntity>,
)
