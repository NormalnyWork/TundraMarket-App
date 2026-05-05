package com.normalnywork.tundramarket.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

const val DB_TRADING_STATION_TABLE_NAME = "trading_stations"
const val DB_TRADING_STATION_COL_ID = "id"
const val DB_TRADING_STATION_COL_NAME = "name"
const val DB_TRADING_STATION_COL_PHONE = "phone"

@Entity(tableName = DB_TRADING_STATION_TABLE_NAME)
data class TradingStationEntity(
    @PrimaryKey
    @ColumnInfo(name = DB_TRADING_STATION_COL_ID)
    val id: Int,
    @ColumnInfo(name = DB_TRADING_STATION_COL_NAME)
    val name: String,
    @ColumnInfo(name = DB_TRADING_STATION_COL_PHONE)
    val phone: String?,
    @Embedded(prefix = DB_LOCATION_PREFIX)
    val location: LocationEntity,
)
