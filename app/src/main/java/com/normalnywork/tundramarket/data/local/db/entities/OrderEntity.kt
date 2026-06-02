package com.normalnywork.tundramarket.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

const val DB_ORDER_TABLE_NAME = "orders"
const val DB_ORDER_COL_ID = "id"
const val DB_ORDER_COL_SERVER_ID = "server_id"
const val DB_ORDER_COL_NOMAD_ID = "nomad_id"
const val DB_ORDER_COL_TRADING_STATION_ID = "trading_station_id"
const val DB_ORDER_COL_COMMENT = "comment"
const val DB_ORDER_COL_STATUS = "status"
const val DB_ORDER_COL_NETWORK_STATUS = "network_status"
const val DB_ORDER_COL_CREATED_AT = "created_at"

@Entity(
    tableName = DB_ORDER_TABLE_NAME,
    indices = [
        Index(value = [DB_ORDER_COL_SERVER_ID], unique = true),
        Index(DB_ORDER_COL_TRADING_STATION_ID),
        Index(DB_ORDER_COL_STATUS),
        Index(DB_ORDER_COL_CREATED_AT),
    ],
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DB_ORDER_COL_ID)
    val id: Int,
    @ColumnInfo(name = DB_ORDER_COL_SERVER_ID)
    val serverId: Int? = null,
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
    @ColumnInfo(name = DB_ORDER_COL_CREATED_AT)
    val createdAt: Long,
)
