package com.normalnywork.tundramarket.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

const val DB_ORDER_TABLE_NAME = "orders"
const val DB_ORDER_COL_ID = "id"
const val DB_ORDER_COL_NOMAD_ID = "nomad_id"
const val DB_ORDER_COL_TRADING_STATION_ID = "trading_station_id"
const val DB_ORDER_COL_COMMENT = "comment"
const val DB_ORDER_COL_STATUS = "status"
const val DB_ORDER_COL_NETWORK_STATUS = "network_status"

@Entity(tableName = DB_ORDER_TABLE_NAME)
data class OrderEntity(
    @PrimaryKey
    @ColumnInfo(name = DB_ORDER_COL_ID)
    val id: Int,
    @ColumnInfo(name = DB_ORDER_COL_NOMAD_ID)
    val nomadId: Int,
    @ColumnInfo(name = DB_ORDER_COL_TRADING_STATION_ID)
    val tradingStationId: Int,
    @Embedded(prefix = DB_LOCATION_PREFIX)
    val location: LocationEntity,
    @ColumnInfo(name = DB_ORDER_COL_COMMENT)
    val comment: String,
    @ColumnInfo(name = DB_ORDER_COL_STATUS)
    val status: OrderStatusEntity,
    @ColumnInfo(name = DB_ORDER_COL_NETWORK_STATUS)
    val networkStatus: OrderNetworkStatusEntity? = null,
)
