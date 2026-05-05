package com.normalnywork.tundramarket.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

const val DB_ORDER_PRODUCT_TABLE_NAME = "order_products"
const val DB_ORDER_PRODUCT_COL_ORDER_ID = "order_id"
const val DB_ORDER_PRODUCT_COL_PRODUCT_ID = "product_id"
const val DB_ORDER_PRODUCT_COL_COUNT = "count"

@Entity(
    tableName = DB_ORDER_PRODUCT_TABLE_NAME,
    primaryKeys = [DB_ORDER_PRODUCT_COL_ORDER_ID, DB_ORDER_PRODUCT_COL_PRODUCT_ID],
)
data class OrderProductEntity(
    @ColumnInfo(name = DB_ORDER_PRODUCT_COL_ORDER_ID)
    val orderId: Int,
    @ColumnInfo(name = DB_ORDER_PRODUCT_COL_PRODUCT_ID)
    val productId: Int,
    @ColumnInfo(name = DB_ORDER_PRODUCT_COL_COUNT)
    val count: Int,
)
