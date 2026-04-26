package com.normalnywork.tundramarket.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val DB_ORDER_STATUS_HISTORY_TABLE_NAME = "order_status_history"
const val DB_ORDER_STATUS_HISTORY_COL_ID = "id"
const val DB_ORDER_STATUS_HISTORY_COL_ORDER_ID = "order_id"
const val DB_ORDER_STATUS_HISTORY_COL_STATUS = "status"
const val DB_ORDER_STATUS_HISTORY_COL_TIME = "time"

@Entity(tableName = DB_ORDER_STATUS_HISTORY_TABLE_NAME)
data class OrderStatusHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DB_ORDER_STATUS_HISTORY_COL_ID)
    val id: Int = 0,
    @ColumnInfo(name = DB_ORDER_STATUS_HISTORY_COL_ORDER_ID)
    val orderId: Int,
    @ColumnInfo(name = DB_ORDER_STATUS_HISTORY_COL_STATUS)
    val status: OrderStatusEntity,
    @ColumnInfo(name = DB_ORDER_STATUS_HISTORY_COL_TIME)
    val time: Long,
)
