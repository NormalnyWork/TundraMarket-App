package com.normalnywork.tundramarket.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.normalnywork.tundramarket.data.local.db.dao.OrdersDao
import com.normalnywork.tundramarket.data.local.db.dao.ProductsDao
import com.normalnywork.tundramarket.data.local.db.dao.SyncOutboxDao
import com.normalnywork.tundramarket.data.local.db.dao.TradingStationsDao
import com.normalnywork.tundramarket.data.local.db.entities.OrderEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderProductEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusHistoryEntity
import com.normalnywork.tundramarket.data.local.db.entities.ProductEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOutboxEntity
import com.normalnywork.tundramarket.data.local.db.entities.TradingStationEntity
import org.koin.core.annotation.Singleton

@Database(
    entities = [
        ProductEntity::class,
        TradingStationEntity::class,
        OrderEntity::class,
        OrderProductEntity::class,
        OrderStatusHistoryEntity::class,
        SyncOutboxEntity::class,
    ],
    version = 1,
)
abstract class TMDatabase : RoomDatabase() {

    abstract fun productsDao(): ProductsDao

    abstract fun tradingStationsDao(): TradingStationsDao

    abstract fun ordersDao(): OrdersDao

    abstract fun syncOutboxDao(): SyncOutboxDao
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

@Singleton
fun provideProductsDao(database: TMDatabase): ProductsDao {
    return database.productsDao()
}

@Singleton
fun provideOrdersDao(database: TMDatabase): OrdersDao {
    return database.ordersDao()
}

@Singleton
fun provideSyncOutboxDao(database: TMDatabase): SyncOutboxDao {
    return database.syncOutboxDao()
}
