package com.normalnywork.tundramarket.data

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.normalnywork.tundramarket.data.local.db.TMDatabase
import com.normalnywork.tundramarket.data.local.db.dao.OrdersDao
import com.normalnywork.tundramarket.data.local.db.dao.SyncOutboxDao
import com.normalnywork.tundramarket.data.local.db.dao.TradingStationsDao
import com.normalnywork.tundramarket.data.local.db.entities.OrderEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderNetworkStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderProductEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusHistoryEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOperationTypeEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOutboxEntity
import com.normalnywork.tundramarket.data.local.db.mappers.toDomain
import com.normalnywork.tundramarket.data.local.db.mappers.toEntity
import com.normalnywork.tundramarket.data.local.db.mappers.toOrderProductEntity
import com.normalnywork.tundramarket.data.local.preferences.OrderHistorySyncStore
import com.normalnywork.tundramarket.data.remote.sms.TmSmsOrder
import com.normalnywork.tundramarket.data.remote.sms.TmSmsOrderCartItem
import com.normalnywork.tundramarket.data.remote.sms.TmSmsOrderCodecs
import com.normalnywork.tundramarket.data.remote.sms.TmSmsOrderEncodeResult
import com.normalnywork.tundramarket.data.remote.sms.TmSmsOrderLengthResult
import com.normalnywork.tundramarket.data.remote.sms.TmSmsOrderValidationError
import com.normalnywork.tundramarket.data.remote.source.RemoteOrdersDataSource
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderNetworkStatus
import com.normalnywork.tundramarket.domain.entities.OrderSmsCommentState
import com.normalnywork.tundramarket.domain.entities.OrderSmsSendState
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.TradingStationOrdersPage
import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import com.normalnywork.tundramarket.sync.SyncWorkScheduler
import com.normalnywork.tundramarket.utils.NetworkStatusObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.annotation.Singleton
import java.util.UUID
import kotlin.coroutines.resume

@Singleton
class OrderRepositoryImpl(
    private val context: Context,
    private val database: TMDatabase,
    private val ordersDao: OrdersDao,
    private val syncOutboxDao: SyncOutboxDao,
    private val tradingStationsDao: TradingStationsDao,
    private val remoteOrdersDataSource: RemoteOrdersDataSource,
    private val orderHistorySyncStore: OrderHistorySyncStore,
    private val syncWorkScheduler: SyncWorkScheduler,
    private val networkStatusObserver: NetworkStatusObserver,
) : OrderRepository {

    override fun getCurrentOrder(): Flow<Order?> {
        return ordersDao.getLatestOrderByStatuses(CURRENT_ORDER_STATUSES)
            .map { orderWithDetails -> orderWithDetails?.toDomain() }
    }

    override fun getOrder(orderId: Int): Flow<Order?> {
        return ordersDao.getOrderByLocalOrServerIdFlow(orderId)
            .map { orderWithDetails -> orderWithDetails?.toDomain() }
    }

    override suspend fun initializeCurrentOrder() {
        val remoteOrder = remoteOrdersDataSource.getCurrentOrder() ?: return
        val tradingStation = tradingStationsDao.getTradingStationById(remoteOrder.tradingStationId)
            ?: error("Trading station ${remoteOrder.tradingStationId} is not cached")
        val remoteLastUpdated = remoteOrder.statusHistory.maxOfOrNull { it.time }

        database.withTransaction {
            val localOrderId = upsertRemoteOrder(
                remoteOrder = remoteOrder,
                tradingStationId = tradingStation.id,
            )

            val assembledProductIds = ordersDao.getAssembledProductIds(localOrderId).toSet()

            ordersDao.deleteOrderProducts(localOrderId)
            ordersDao.deleteStatusHistory(localOrderId)
            ordersDao.insertOrderProducts(
                remoteOrder.cart.map { productCount ->
                    OrderProductEntity(
                        orderId = localOrderId,
                        productId = productCount.productId,
                        count = productCount.count,
                        isAssembled = productCount.productId in assembledProductIds,
                    )
                },
            )
            ordersDao.insertStatusHistory(
                remoteOrder.statusHistory.map { history ->
                    OrderStatusHistoryEntity(
                        orderId = localOrderId,
                        status = OrderStatusEntity.valueOf(history.status.name),
                        time = history.time,
                        comment = history.comment,
                    )
                },
            )
        }

        if (remoteLastUpdated != null) {
            orderHistorySyncStore.setCurrentOrderStatusLastUpdated(
                orderId = remoteOrder.id,
                lastUpdated = remoteLastUpdated,
            )
        }
    }

    override suspend fun createOrder(order: Order) {
        val now = System.currentTimeMillis()

        database.withTransaction {
            val localOrder = order.copy(
                id = 0,
                networkStatus = if (networkStatusObserver.isConnected()) {
                    OrderNetworkStatus.Loading
                } else {
                    OrderNetworkStatus.Failed
                },
            )
            val localOrderId = ordersDao.insertOrder(
                localOrder.toEntity(
                    id = 0,
                    createdAt = now,
                ),
            ).toInt()

            ordersDao.insertOrderProducts(
                localOrder.cart.map { it.toOrderProductEntity(orderId = localOrderId) },
            )
            ordersDao.insertStatusHistory(
                localOrder.statusHistory.map { it.toEntity(orderId = localOrderId) },
            )
            syncOutboxDao.insert(
                SyncOutboxEntity(
                    operationType = SyncOperationTypeEntity.CreateOrder,
                    localEntityId = localOrderId,
                    nextAttemptAt = now,
                    createdAt = now,
                    idempotencyKey = UUID.randomUUID().toString(),
                ),
            )
        }

        syncWorkScheduler.schedule()
    }

    override suspend fun sendOrderViaSms(
        order: Order,
        comment: String,
    ) {
        val phone = order.tradingStation.phone ?: return
        val localOrder = ordersDao.getOrderByLocalOrServerId(order.id) ?: return
        val localOrderId = localOrder.order.id
        val message = when (val encodeResult = TmSmsOrderCodecs.encode(order.toSmsOrder(comment))) {
            is TmSmsOrderEncodeResult.Success -> encodeResult.message
            is TmSmsOrderEncodeResult.Failure -> {
                ordersDao.updateNetworkStatus(
                    localOrderId = localOrderId,
                    networkStatus = OrderNetworkStatusEntity.SmsFailed,
                )
                return
            }
        }

        ordersDao.updateCommentAndNetworkStatus(
            localOrderId = localOrderId,
            comment = comment,
            networkStatus = OrderNetworkStatusEntity.LoadingSms,
        )

        val isSent = sendSms(
            phone = phone,
            message = message,
        )

        database.withTransaction {
            ordersDao.updateNetworkStatus(
                localOrderId = localOrderId,
                networkStatus = if (isSent) null else OrderNetworkStatusEntity.SmsFailed,
            )

            if (isSent) {
                syncOutboxDao.deleteByLocalEntityIdAndOperationType(
                    localEntityId = localOrderId,
                    operationType = SyncOperationTypeEntity.CreateOrder,
                )
            }
        }

        if (isSent) {
            syncWorkScheduler.schedule(
                replace = true,
                syncCurrentOrderStatus = true,
            )
        }
    }

    override fun getOrderSmsSendState(order: Order): OrderSmsSendState {
        val comment = order.comment.trim()
        return when {
            order.canBeSentViaSms(comment) -> OrderSmsSendState.Ready(comment)
            order.shouldOfferSmsCommentEdit(comment) -> OrderSmsSendState.CommentEditRequired(comment)
            else -> OrderSmsSendState.Unavailable
        }
    }

    override fun getOrderSmsCommentState(
        order: Order,
        comment: String,
    ): OrderSmsCommentState {
        val smsLength = when (val lengthResult = TmSmsOrderCodecs.calculateSmsLength(order.toSmsOrder(comment))) {
            is TmSmsOrderLengthResult.Success -> lengthResult.smsLength
            is TmSmsOrderLengthResult.Failure -> null
        }

        return OrderSmsCommentState(
            smsLength = smsLength,
            smsLimit = TmSmsOrderCodecs.singleSmsLimit,
            canSend = order.canBeSentViaSms(comment),
        )
    }

    override suspend fun updateCurrentOrderStatus() {
        syncWorkScheduler.schedule(
            replace = true,
            syncCurrentOrderStatus = true,
        )
    }

    override suspend fun changeOrderStatus(
        order: Order,
        comment: String?,
    ) {
        val localOrder = ordersDao.getOrderByLocalOrServerId(order.id) ?: return
        val localOrderId = localOrder.order.id
        val status = OrderStatusEntity.valueOf(order.status.name)
        val now = System.currentTimeMillis()
        var shouldScheduleSync = true

        database.withTransaction {
            if (localOrder.order.serverId == null && status == OrderStatusEntity.Cancelled) {
                ordersDao.updateStatus(
                    localOrderId = localOrderId,
                    status = status,
                )
                ordersDao.updateNetworkStatus(
                    localOrderId = localOrderId,
                    networkStatus = null,
                )
                ordersDao.insertStatusHistory(
                    listOf(
                        OrderStatusHistoryEntity(
                            orderId = localOrderId,
                            status = status,
                            time = now,
                            comment = comment,
                        ),
                    ),
                )
                syncOutboxDao.deleteByLocalEntityId(localOrderId)
                shouldScheduleSync = false
                return@withTransaction
            }

            ordersDao.updateStatus(
                localOrderId = localOrderId,
                status = status,
            )
            ordersDao.updateNetworkStatus(
                localOrderId = localOrderId,
                networkStatus = if (networkStatusObserver.isConnected()) {
                    OrderNetworkStatusEntity.Updating
                } else {
                    OrderNetworkStatusEntity.UpdateFailed
                },
            )
            ordersDao.insertStatusHistory(
                listOf(
                    OrderStatusHistoryEntity(
                        orderId = localOrderId,
                        status = status,
                        time = now,
                        comment = comment,
                    ),
                ),
            )
            syncOutboxDao.insert(
                SyncOutboxEntity(
                    operationType = SyncOperationTypeEntity.ChangeOrderStatus,
                    localEntityId = localOrderId,
                    nextAttemptAt = now,
                    createdAt = now,
                    idempotencyKey = UUID.randomUUID().toString(),
                    comment = comment,
                ),
            )
        }

        if (shouldScheduleSync) {
            syncWorkScheduler.schedule()
        }
    }

    override suspend fun setOrderProductAssembled(
        order: Order,
        productId: Int,
        isAssembled: Boolean,
    ) {
        val localOrder = ordersDao.getOrderByLocalOrServerId(order.id) ?: return

        ordersDao.updateOrderProductAssembled(
            localOrderId = localOrder.order.id,
            productId = productId,
            isAssembled = isAssembled,
        )
    }

    override fun getProcessingOrders(): Flow<PagingData<Order>> {
        return getOrdersByStatuses(PROCESSING_ORDER_STATUSES)
    }

    override fun getNewOrders(): Flow<PagingData<Order>> {
        return getOrdersByStatuses(NEW_ORDER_STATUSES)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getHistoryOrders(): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(
                pageSize = HISTORY_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            remoteMediator = NomadHistoryRemoteMediator(
                database = database,
                ordersDao = ordersDao,
                tradingStationsDao = tradingStationsDao,
                remoteOrdersDataSource = remoteOrdersDataSource,
                orderHistorySyncStore = orderHistorySyncStore,
                networkStatusObserver = networkStatusObserver,
                historyOrderStatuses = NOMAD_HISTORY_ORDER_STATUSES,
                pageSize = HISTORY_PAGE_SIZE,
            ),
            pagingSourceFactory = {
                ordersDao.getOrdersByStatusesPaged(NOMAD_HISTORY_ORDER_STATUSES)
            },
        ).flow.map { pagingData ->
            pagingData.map { orderWithDetails -> orderWithDetails.toDomain() }
        }
    }

    override fun getTradingStationHistoryOrders(): Flow<PagingData<Order>> {
        return getOrdersByStatuses(TRADING_STATION_HISTORY_ORDER_STATUSES)
    }

    override fun hasNewOrders(): Flow<Boolean> {
        return ordersDao.getOrderCountByStatuses(NEW_ORDER_STATUSES)
            .map { count -> count > 0 }
    }

    override suspend fun updateOrders() {
        if (!networkStatusObserver.isConnected()) return

        if (!orderHistorySyncStore.areTradingStationPagesFullyCached()) {
            cacheTradingStationPages()
        }

        if (orderHistorySyncStore.areTradingStationPagesFullyCached()) {
            val page = remoteOrdersDataSource.getOrderUpdates(
                lastUpdated = orderHistorySyncStore.getTradingStationOrdersLastUpdated(),
            )

            database.withTransaction {
                page.orders.forEach { remoteOrder ->
                    upsertRemoteOrderWithDetails(remoteOrder)
                }
            }
            updateTradingStationOrdersLastUpdated(page.orders)
        }
    }

    private fun getOrdersByStatuses(statuses: List<OrderStatusEntity>): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(
                pageSize = ORDERS_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                ordersDao.getOrdersByStatusesPaged(statuses)
            },
        ).flow.map { pagingData ->
            pagingData.map { orderWithDetails -> orderWithDetails.toDomain() }
        }
    }

    private suspend fun cacheTradingStationPages() {
        TradingStationOrdersPage.entries.forEach { page ->
            if (!orderHistorySyncStore.isTradingStationPageFullyCached(page)) {
                cacheTradingStationPage(page)
            }
        }
    }

    private suspend fun cacheTradingStationPage(page: TradingStationOrdersPage) {
        val statuses = page.toOrderStatuses()

        while (!orderHistorySyncStore.isTradingStationPageFullyCached(page)) {
            val anchor = ordersDao.getOldestServerIdByStatuses(statuses)
            val remotePage = remoteOrdersDataSource.getTradingStationOrders(
                page = page,
                anchor = anchor,
                pageSize = ORDERS_PAGE_SIZE,
            )

            database.withTransaction {
                remotePage.orders.forEach { remoteOrder ->
                    upsertRemoteOrderWithDetails(remoteOrder)
                }
            }
            updateTradingStationOrdersLastUpdated(remotePage.orders)

            if (remotePage.orders.size < ORDERS_PAGE_SIZE) {
                orderHistorySyncStore.markTradingStationPageFullyCached(page)
            }
        }
    }

    private suspend fun upsertRemoteOrderWithDetails(remoteOrder: RemoteOrdersDataSource.OrderListItem): Int {
        val tradingStation = tradingStationsDao.getTradingStationById(remoteOrder.tradingStationId)
            ?: error("Trading station ${remoteOrder.tradingStationId} is not cached")
        val localOrderId = upsertRemoteOrder(
            remoteOrder = remoteOrder,
            tradingStationId = tradingStation.id,
        )

        val assembledProductIds = ordersDao.getAssembledProductIds(localOrderId).toSet()

        ordersDao.deleteOrderProducts(localOrderId)
        ordersDao.deleteStatusHistory(localOrderId)
        ordersDao.insertOrderProducts(
            remoteOrder.cart.map { productCount ->
                OrderProductEntity(
                    orderId = localOrderId,
                    productId = productCount.productId,
                    count = productCount.count,
                    isAssembled = productCount.productId in assembledProductIds,
                )
            },
        )
        ordersDao.insertStatusHistory(
            remoteOrder.statusHistory.map { history ->
                OrderStatusHistoryEntity(
                    orderId = localOrderId,
                    status = OrderStatusEntity.valueOf(history.status.name),
                    time = history.time,
                    comment = history.comment,
                )
            },
        )

        return localOrderId
    }

    private suspend fun updateTradingStationOrdersLastUpdated(
        orders: List<RemoteOrdersDataSource.OrderListItem>,
    ) {
        val remoteLastUpdated = orders
            .flatMap { order -> order.statusHistory }
            .maxOfOrNull { history -> history.time }
            ?: return
        val cachedLastUpdated = orderHistorySyncStore.getTradingStationOrdersLastUpdated()

        if (remoteLastUpdated > cachedLastUpdated) {
            orderHistorySyncStore.setTradingStationOrdersLastUpdated(remoteLastUpdated)
        }
    }

    private fun TradingStationOrdersPage.toOrderStatuses() = when (this) {
        TradingStationOrdersPage.Active -> PROCESSING_ORDER_STATUSES
        TradingStationOrdersPage.New -> NEW_ORDER_STATUSES
        TradingStationOrdersPage.History -> TRADING_STATION_HISTORY_ORDER_STATUSES
    }

    private suspend fun upsertRemoteOrder(
        remoteOrder: RemoteOrdersDataSource.OrderListItem,
        tradingStationId: Int,
    ): Int {
        val existingOrder = ordersDao.getOrderByServerId(remoteOrder.id)?.order
        val createdAt = remoteOrder.statusHistory.minOfOrNull { it.time } ?: System.currentTimeMillis()
        val order = OrderEntity(
            id = existingOrder?.id ?: 0,
            serverId = remoteOrder.id,
            nomadId = remoteOrder.nomadId,
            tradingStationId = tradingStationId,
            location = remoteOrder.location.toEntity(),
            comment = remoteOrder.comment,
            status = OrderStatusEntity.valueOf(remoteOrder.status.name),
            networkStatus = null,
            createdAt = existingOrder?.createdAt ?: createdAt,
        )

        return if (existingOrder == null) {
            ordersDao.insertOrder(order).toInt()
        } else {
            ordersDao.updateOrder(order)
            existingOrder.id
        }
    }

    @Suppress("DEPRECATION")
    private val smsManager: SmsManager
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            SmsManager.getDefault()
        }

    private fun Order.toSmsOrder(comment: String): TmSmsOrder {
        return TmSmsOrder(
            clientOrderId = id,
            location = location,
            cart = cart.map { (product, quantity) ->
                TmSmsOrderCartItem(
                    productId = product.id,
                    quantity = quantity,
                )
            },
            comment = comment,
        )
    }

    private fun Order.canBeSentViaSms(comment: String): Boolean {
        return TmSmsOrderCodecs.encode(toSmsOrder(comment)) is TmSmsOrderEncodeResult.Success
    }

    private fun Order.shouldOfferSmsCommentEdit(comment: String): Boolean {
        val encodeResult = TmSmsOrderCodecs.encode(toSmsOrder(comment))
        if (encodeResult !is TmSmsOrderEncodeResult.Failure) return false

        val canBeFixedByComment = encodeResult.errors.any { error ->
            error == TmSmsOrderValidationError.SmsLengthExceeded ||
                error == TmSmsOrderValidationError.InvalidComment
        }

        return canBeFixedByComment && canBeSentViaSms(comment = "")
    }

    private suspend fun sendSms(
        phone: String,
        message: String,
    ): Boolean {
        return withTimeoutOrNull(SMS_SEND_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                val action = "${context.packageName}.SMS_SENT.${System.nanoTime()}"
                val intent = Intent(action).setPackage(context.packageName)
                val sentIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
                val receiver = object : BroadcastReceiver() {

                    override fun onReceive(
                        context: Context,
                        intent: Intent,
                    ) {
                        runCatching { context.unregisterReceiver(this) }
                        if (continuation.isActive) {
                            continuation.resume(resultCode == Activity.RESULT_OK)
                        }
                    }
                }

                ContextCompat.registerReceiver(
                    context,
                    receiver,
                    IntentFilter(action),
                    ContextCompat.RECEIVER_NOT_EXPORTED,
                )
                continuation.invokeOnCancellation {
                    runCatching { context.unregisterReceiver(receiver) }
                }

                runCatching {
                    smsManager.sendTextMessage(
                        phone,
                        null,
                        message,
                        sentIntent,
                        null,
                    )
                }.onFailure {
                    runCatching { context.unregisterReceiver(receiver) }
                    if (continuation.isActive) {
                        continuation.resume(false)
                    }
                }
            }
        } ?: false
    }

    private companion object {

        const val SMS_SEND_TIMEOUT_MS = 30_000L
        const val ORDERS_PAGE_SIZE = 20
        const val HISTORY_PAGE_SIZE = ORDERS_PAGE_SIZE

        val CURRENT_ORDER_STATUSES = listOf(
            OrderStatus.Created,
            OrderStatus.Processing,
            OrderStatus.Sent,
            OrderStatus.Completed,
            OrderStatus.Cancelled,
            OrderStatus.Denied,
        ).map { OrderStatusEntity.valueOf(it.name) }

        val NEW_ORDER_STATUSES = listOf(
            OrderStatus.Created,
        ).map { OrderStatusEntity.valueOf(it.name) }

        val PROCESSING_ORDER_STATUSES = listOf(
            OrderStatus.Processing,
            OrderStatus.Sent,
        ).map { OrderStatusEntity.valueOf(it.name) }

        val NOMAD_HISTORY_ORDER_STATUSES = listOf(
            OrderStatus.Created,
            OrderStatus.Processing,
            OrderStatus.Sent,
            OrderStatus.Completed,
            OrderStatus.Cancelled,
            OrderStatus.Denied,
        ).map { OrderStatusEntity.valueOf(it.name) }

        val TRADING_STATION_HISTORY_ORDER_STATUSES = listOf(
            OrderStatus.Completed,
            OrderStatus.Cancelled,
            OrderStatus.Denied,
        ).map { OrderStatusEntity.valueOf(it.name) }
    }
}
