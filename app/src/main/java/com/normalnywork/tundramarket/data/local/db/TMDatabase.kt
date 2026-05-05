package com.normalnywork.tundramarket.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.normalnywork.tundramarket.data.local.db.dao.TradingStationsDao
import com.normalnywork.tundramarket.data.local.db.entities.TradingStationEntity
import org.koin.core.annotation.Singleton

@Database(
    entities = [
        TradingStationEntity::class,
    ],
    version = 1,
)
abstract class TMDatabase : RoomDatabase() {

    abstract fun tradingStationsDao(): TradingStationsDao
}

@Singleton
fun provideDatabase(context: Context): TMDatabase {
    return Room.databaseBuilder(context, TMDatabase::class.java, "tm-db")
        .build()
}

@Singleton
fun provideTradingStationsDao(database: TMDatabase): TradingStationsDao {
    return database.tradingStationsDao()
}