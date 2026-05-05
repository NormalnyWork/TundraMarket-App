package com.normalnywork.tundramarket.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.normalnywork.tundramarket.data.local.db.entities.DB_PRODUCT_TABLE_NAME
import com.normalnywork.tundramarket.data.local.db.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<ProductEntity>)

    @Query("SELECT * FROM $DB_PRODUCT_TABLE_NAME")
    fun getProducts(): Flow<List<ProductEntity>>
}
