package com.normalnywork.tundramarket.data.local.entities

import androidx.room.ColumnInfo

const val DB_LOCATION_COL_LATITUDE = "latitude"
const val DB_LOCATION_COL_LONGITUDE = "longitude"
const val DB_LOCATION_PREFIX = "loc_"

data class LocationEntity(
    @ColumnInfo(name = DB_LOCATION_COL_LATITUDE)
    val latitude: Float,
    @ColumnInfo(name = DB_LOCATION_COL_LONGITUDE)
    val longitude: Float,
)
