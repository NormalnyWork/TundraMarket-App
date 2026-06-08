package com.normalnywork.tundramarket.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_COL_CREATED_AT
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_COL_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_COL_NETWORK_STATUS
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_COL_SERVER_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_COL_STATUS
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_PRODUCT_COL_ORDER_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_PRODUCT_TABLE_NAME
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_STATUS_HISTORY_COL_ORDER_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_STATUS_HISTORY_TABLE_NAME
import com.normalnywork.tundramarket.data.local.db.entities.DB_ORDER_TABLE_NAME
import com.normalnywork.tundramarket.data.local.db.entities.OrderEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderNetworkStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderProductEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusHistoryEntity
import com.normalnywork.tundramarket.data.local.db.relations.OrderWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderProducts(products: List<OrderProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatusHistory(history: List<OrderStatusHistoryEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Transaction
    @Query(
        """
        SELECT * FROM $DB_ORDER_TABLE_NAME
        WHERE $DB_ORDER_COL_STATUS IN (:statuses)
        ORDER BY $DB_ORDER_COL_CREATED_AT DESC
        LIMIT 1
        """,
    )
    fun getLatestOrderByStatuses(statuses: List<OrderStatusEntity>): Flow<OrderWithDetails?>

    @Transaction
    @Query(
        """
        SELECT * FROM $DB_ORDER_TABLE_NAME
        WHERE $DB_ORDER_COL_STATUS IN (:statuses)
        ORDER BY $DB_ORDER_COL_CREATED_AT DESC
        LIMIT 1
        """,
    )
    suspend fun getLatestOrderByStatusesOnce(statuses: List<OrderStatusEntity>): OrderWithDetails?

    @Transaction
    @Query("SELECT * FROM $DB_ORDER_TABLE_NAME WHERE $DB_ORDER_COL_ID = :localOrderId")
    suspend fun getOrderByLocalId(localOrderId: Int): OrderWithDetails?

    @Transaction
    @Query("SELECT * FROM $DB_ORDER_TABLE_NAME WHERE $DB_ORDER_COL_SERVER_ID = :serverOrderId")
    suspend fun getOrderByServerId(serverOrderId: Int): OrderWithDetails?

    @Query(
        """
        SELECT $DB_ORDER_COL_SERVER_ID FROM $DB_ORDER_TABLE_NAME
        WHERE $DB_ORDER_COL_STATUS IN (:statuses)
            AND $DB_ORDER_COL_SERVER_ID IS NOT NULL
        ORDER BY $DB_ORDER_COL_CREATED_AT ASC
        LIMIT 1
        """,
    )
    suspend fun getOldestServerIdByStatuses(statuses: List<OrderStatusEntity>): Int?

    @Transaction
    @Query(
        """
        SELECT * FROM $DB_ORDER_TABLE_NAME
        WHERE $DB_ORDER_COL_ID = :orderId OR $DB_ORDER_COL_SERVER_ID = :orderId
        LIMIT 1
        """,
    )
    suspend fun getOrderByLocalOrServerId(orderId: Int): OrderWithDetails?

    @Query(
        """
        UPDATE $DB_ORDER_TABLE_NAME
        SET $DB_ORDER_COL_STATUS = :status
        WHERE $DB_ORDER_COL_ID = :localOrderId
        """,
    )
    suspend fun updateStatus(
        localOrderId: Int,
        status: OrderStatusEntity,
    )

    @Query(
        """
        UPDATE $DB_ORDER_TABLE_NAME
        SET $DB_ORDER_COL_SERVER_ID = :serverOrderId,
            $DB_ORDER_COL_NETWORK_STATUS = NULL
        WHERE $DB_ORDER_COL_ID = :localOrderId
        """,
    )
    suspend fun markOrderSynced(
        localOrderId: Int,
        serverOrderId: Int,
    )

    @Query(
        """
        UPDATE $DB_ORDER_TABLE_NAME
        SET $DB_ORDER_COL_NETWORK_STATUS = :networkStatus
        WHERE $DB_ORDER_COL_ID = :localOrderId
        """,
    )
    suspend fun updateNetworkStatus(
        localOrderId: Int,
        networkStatus: OrderNetworkStatusEntity?,
    )

    @Query("DELETE FROM $DB_ORDER_PRODUCT_TABLE_NAME WHERE $DB_ORDER_PRODUCT_COL_ORDER_ID = :localOrderId")
    suspend fun deleteOrderProducts(localOrderId: Int)

    @Query("DELETE FROM $DB_ORDER_STATUS_HISTORY_TABLE_NAME WHERE $DB_ORDER_STATUS_HISTORY_COL_ORDER_ID = :localOrderId")
    suspend fun deleteStatusHistory(localOrderId: Int)

    @Transaction
    @Query(
        """
        SELECT * FROM $DB_ORDER_TABLE_NAME
        WHERE $DB_ORDER_COL_STATUS IN (:statuses)
        ORDER BY $DB_ORDER_COL_CREATED_AT DESC
        """,
    )
    fun getOrdersByStatuses(statuses: List<OrderStatusEntity>): Flow<List<OrderWithDetails>>

    @Transaction
    @Query(
        """
        SELECT * FROM $DB_ORDER_TABLE_NAME
        WHERE $DB_ORDER_COL_STATUS IN (:statuses)
        ORDER BY $DB_ORDER_COL_CREATED_AT DESC
        """,
    )
    fun getOrdersByStatusesPaged(statuses: List<OrderStatusEntity>): PagingSource<Int, OrderWithDetails>

    @Query(
        """
        SELECT COUNT(*) FROM $DB_ORDER_TABLE_NAME
        WHERE $DB_ORDER_COL_STATUS IN (:statuses)
        """,
    )
    fun getOrderCountByStatuses(statuses: List<OrderStatusEntity>): Flow<Int>
}
