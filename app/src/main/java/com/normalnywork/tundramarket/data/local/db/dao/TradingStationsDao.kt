package com.normalnywork.tundramarket.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.normalnywork.tundramarket.data.local.db.entities.DB_TRADING_STATION_TABLE_NAME
import com.normalnywork.tundramarket.data.local.db.entities.TradingStationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TradingStationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<TradingStationEntity>)

    @Query("SELECT * FROM $DB_TRADING_STATION_TABLE_NAME")
    fun getTradingStations(): Flow<List<TradingStationEntity>>
}