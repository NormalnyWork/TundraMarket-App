package com.normalnywork.tundramarket.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.normalnywork.tundramarket.data.local.db.TMDatabase
import com.normalnywork.tundramarket.data.local.db.dao.OrdersDao
import com.normalnywork.tundramarket.data.local.db.dao.TradingStationsDao
import com.normalnywork.tundramarket.data.local.db.entities.OrderEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderProductEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusHistoryEntity
import com.normalnywork.tundramarket.data.local.db.mappers.toEntity
import com.normalnywork.tundramarket.data.local.db.relations.OrderWithDetails
import com.normalnywork.tundramarket.data.local.preferences.OrderHistorySyncStore
import com.normalnywork.tundramarket.data.remote.source.RemoteOrdersDataSource
import com.normalnywork.tundramarket.utils.NetworkStatusObserver

@OptIn(ExperimentalPagingApi::class)
class NomadHistoryRemoteMediator(
    private val database: TMDatabase,
    private val ordersDao: OrdersDao,
    private val tradingStationsDao: TradingStationsDao,
    private val remoteOrdersDataSource: RemoteOrdersDataSource,
    private val orderHistorySyncStore: OrderHistorySyncStore,
    private val networkStatusObserver: NetworkStatusObserver,
    private val historyOrderStatuses: List<OrderStatusEntity>,
    private val pageSize: Int,
) : RemoteMediator<Int, OrderWithDetails>() {

    override suspend fun initialize(): InitializeAction {
        return if (orderHistorySyncStore.isHistoryFullyCached()) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, OrderWithDetails>,
    ): MediatorResult {
        if (orderHistorySyncStore.isHistoryFullyCached()) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        if (loadType == LoadType.PREPEND) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        val anchor = ordersDao.getOldestServerIdByStatuses(historyOrderStatuses)
        if (loadType == LoadType.REFRESH && anchor != null) {
            return MediatorResult.Success(endOfPaginationReached = false)
        }

        if (!networkStatusObserver.isConnected()) {
            return MediatorResult.Error(NoInternetException())
        }

        return runCatching {
            val page = remoteOrdersDataSource.getHistoryOrders(
                anchor = anchor,
                pageSize = pageSize,
            )

            database.withTransaction {
                page.orders.forEach { remoteOrder ->
                    insertRemoteOrderIfMissing(remoteOrder)
                }
            }

            val isFullyCached = page.orders.size < pageSize
            if (isFullyCached) {
                orderHistorySyncStore.markHistoryFullyCached()
            }
            MediatorResult.Success(endOfPaginationReached = isFullyCached)
        }.getOrElse { error ->
            MediatorResult.Error(error)
        }
    }

    private suspend fun insertRemoteOrderIfMissing(remoteOrder: RemoteOrdersDataSource.OrderListItem) {
        if (ordersDao.getOrderByServerId(remoteOrder.id) != null) return
        val tradingStation = tradingStationsDao.getTradingStationById(remoteOrder.tradingStationId)
            ?: error("Trading station ${remoteOrder.tradingStationId} is not cached")

        val createdAt = remoteOrder.statusHistory.minOfOrNull { it.time } ?: System.currentTimeMillis()
        val savedOrderId = ordersDao.insertOrder(
            OrderEntity(
                id = 0,
                serverId = remoteOrder.id,
                nomadId = remoteOrder.nomadId,
                tradingStationId = tradingStation.id,
                location = remoteOrder.location.toEntity(),
                comment = remoteOrder.comment,
                status = OrderStatusEntity.valueOf(remoteOrder.status.name),
                createdAt = createdAt,
            ),
        ).toInt()

        ordersDao.insertOrderProducts(
            remoteOrder.cart.map { productCount ->
                OrderProductEntity(
                    orderId = savedOrderId,
                    productId = productCount.productId,
                    count = productCount.count,
                )
            },
        )
        ordersDao.insertStatusHistory(
            remoteOrder.statusHistory.map { history ->
                OrderStatusHistoryEntity(
                    orderId = savedOrderId,
                    status = OrderStatusEntity.valueOf(history.status.name),
                    time = history.time,
                    comment = history.comment,
                )
            },
        )
    }

    private class NoInternetException : Exception()
}
