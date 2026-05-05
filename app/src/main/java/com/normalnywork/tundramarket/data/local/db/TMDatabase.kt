package com.normalnywork.tundramarket.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.normalnywork.tundramarket.data.local.db.dao.ProductsDao
import com.normalnywork.tundramarket.data.local.db.dao.TradingStationsDao
import com.normalnywork.tundramarket.data.local.db.entities.DB_PRODUCT_COL_DETAILS
import com.normalnywork.tundramarket.data.local.db.entities.DB_PRODUCT_COL_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_PRODUCT_COL_NAME
import com.normalnywork.tundramarket.data.local.db.entities.DB_PRODUCT_COL_VOLUME
import com.normalnywork.tundramarket.data.local.db.entities.DB_PRODUCT_COL_WEIGHT
import com.normalnywork.tundramarket.data.local.db.entities.DB_PRODUCT_TABLE_NAME
import com.normalnywork.tundramarket.data.local.db.entities.ProductEntity
import com.normalnywork.tundramarket.data.local.db.entities.TradingStationEntity
import org.koin.core.annotation.Singleton

@Database(
    entities = [
        ProductEntity::class,
        TradingStationEntity::class,
    ],
    version = 2,
)
abstract class TMDatabase : RoomDatabase() {

    abstract fun productsDao(): ProductsDao

    abstract fun tradingStationsDao(): TradingStationsDao
}

@Singleton
fun provideDatabase(context: Context): TMDatabase {
    return Room.databaseBuilder(context, TMDatabase::class.java, "tm-db")
        .addMigrations(MIGRATION_1_2)
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

private val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `$DB_PRODUCT_TABLE_NAME` (
                `$DB_PRODUCT_COL_ID` INTEGER NOT NULL,
                `$DB_PRODUCT_COL_NAME` TEXT NOT NULL,
                `$DB_PRODUCT_COL_DETAILS` TEXT,
                `$DB_PRODUCT_COL_WEIGHT` REAL NOT NULL,
                `$DB_PRODUCT_COL_VOLUME` REAL NOT NULL,
                PRIMARY KEY(`$DB_PRODUCT_COL_ID`)
            )
            """.trimIndent(),
        )
    }
}
