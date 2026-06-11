package com.normalnywork.tundramarket.data

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
import com.normalnywork.tundramarket.data.local.db.mappers.toEntity
import com.normalnywork.tundramarket.data.local.preferences.UserSessionStore
import com.normalnywork.tundramarket.data.remote.sms.TmSmsOrder
import com.normalnywork.tundramarket.data.remote.sms.TmSmsOrderCodecs
import com.normalnywork.tundramarket.data.remote.sms.TmSmsOrderDecodeResult
import com.normalnywork.tundramarket.utils.NetworkStatusObserver
import com.normalnywork.tundramarket.utils.sync.SyncWorkScheduler
import org.koin.core.annotation.Singleton
import java.nio.charset.StandardCharsets
import java.util.UUID

@Singleton
class IncomingSmsOrderHandler(
    private val database: TMDatabase,
    private val ordersDao: OrdersDao,
    private val syncOutboxDao: SyncOutboxDao,
    private val tradingStationsDao: TradingStationsDao,
    private val userSessionStore: UserSessionStore,
    private val networkStatusObserver: NetworkStatusObserver,
    private val syncWorkScheduler: SyncWorkScheduler,
) {

    suspend fun handle(
        senderPhone: String?,
        message: String,
    ) {
        val nomadPhone = senderPhone
            ?.filter { it.isDigit() }
            ?.replaceFirstChar { "7" }
            ?.takeIf { it.isNotBlank() } ?: return
        if (!TmSmsOrderCodecs.isEncodedOrderMessage(message)) return

        val smsOrder = when (val decodeResult = TmSmsOrderCodecs.decode(message)) {
            is TmSmsOrderDecodeResult.Success -> decodeResult.order
            is TmSmsOrderDecodeResult.Failure -> return
        }
        val tradingStationId = userSessionStore.getTradingStationId() ?: return
        tradingStationsDao.getTradingStationById(tradingStationId) ?: return

        val wasEnqueued = saveOrderAndEnqueueSync(
            nomadPhone = nomadPhone,
            smsOrder = smsOrder,
            tradingStationId = tradingStationId,
        )

        if (wasEnqueued) {
            syncWorkScheduler.schedule()
        }
    }

    private suspend fun saveOrderAndEnqueueSync(
        nomadPhone: String,
        smsOrder: TmSmsOrder,
        tradingStationId: Int,
    ): Boolean {
        val now = System.currentTimeMillis()
        val idempotencyKey = smsOrder.toIdempotencyKey(nomadPhone)

        return database.withTransaction {
            val localOrderId = ordersDao.insertOrder(
                OrderEntity(
                    id = 0,
                    serverId = null,
                    nomadId = LOCAL_NOMAD_ID,
                    tradingStationId = tradingStationId,
                    location = smsOrder.location.toEntity(),
                    comment = smsOrder.comment.orEmpty(),
                    status = OrderStatusEntity.Created,
                    networkStatus = if (networkStatusObserver.isConnected()) {
                        OrderNetworkStatusEntity.Loading
                    } else {
                        OrderNetworkStatusEntity.Failed
                    },
                    createdAt = now,
                ),
            ).toInt()

            ordersDao.insertOrderProducts(
                smsOrder.cart.map { item ->
                    OrderProductEntity(
                        orderId = localOrderId,
                        productId = item.productId,
                        count = item.quantity,
                    )
                },
            )
            ordersDao.insertStatusHistory(
                listOf(
                    OrderStatusHistoryEntity(
                        orderId = localOrderId,
                        status = OrderStatusEntity.Created,
                        time = now,
                    ),
                ),
            )

            val outboxId = syncOutboxDao.insertIgnore(
                SyncOutboxEntity(
                    operationType = SyncOperationTypeEntity.CreateOrderForNomad,
                    localEntityId = localOrderId,
                    nextAttemptAt = now,
                    createdAt = now,
                    idempotencyKey = idempotencyKey,
                    comment = nomadPhone,
                ),
            )

            if (outboxId == DUPLICATE_OUTBOX_ID) {
                ordersDao.deleteOrderProducts(localOrderId)
                ordersDao.deleteStatusHistory(localOrderId)
                ordersDao.deleteOrder(localOrderId)
                false
            } else {
                true
            }
        }
    }

    private fun TmSmsOrder.toIdempotencyKey(nomadPhone: String): String {
        val key = "$IDEMPOTENCY_SOURCE:$nomadPhone:$clientOrderId"
        return UUID.nameUUIDFromBytes(key.toByteArray(StandardCharsets.UTF_8)).toString()
    }

    private companion object {

        const val LOCAL_NOMAD_ID = 0
        const val DUPLICATE_OUTBOX_ID = -1L
        const val IDEMPOTENCY_SOURCE = "sms-order"
    }
}
