package com.normalnywork.tundramarket.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val DB_PRODUCT_TABLE_NAME = "products"
const val DB_PRODUCT_COL_ID = "id"
const val DB_PRODUCT_COL_NAME = "name"
const val DB_PRODUCT_COL_DETAILS = "details"
const val DB_PRODUCT_COL_WEIGHT = "weight"
const val DB_PRODUCT_COL_VOLUME = "volume"

@Entity(tableName = DB_PRODUCT_TABLE_NAME)
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = DB_PRODUCT_COL_ID)
    val id: Int,
    @ColumnInfo(name = DB_PRODUCT_COL_NAME)
    val name: String,
    @ColumnInfo(name = DB_PRODUCT_COL_DETAILS)
    val details: String?,
    @ColumnInfo(name = DB_PRODUCT_COL_WEIGHT)
    val weight: Float,
    @ColumnInfo(name = DB_PRODUCT_COL_VOLUME)
    val volume: Float,
)
