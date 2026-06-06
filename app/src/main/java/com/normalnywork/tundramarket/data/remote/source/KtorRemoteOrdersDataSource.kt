package com.normalnywork.tundramarket.data.remote.source

import com.normalnywork.tundramarket.data.remote.api.mappers.toChangeStatusRequest
import com.normalnywork.tundramarket.data.remote.api.mappers.toCreateRequest
import com.normalnywork.tundramarket.data.remote.api.mappers.toOrderListPage
import com.normalnywork.tundramarket.data.remote.api.mappers.toOrderStatusUpdates
import com.normalnywork.tundramarket.data.remote.api.proto.ChangeOrderStatusResponse
import com.normalnywork.tundramarket.data.remote.api.proto.CheckOrderStatusRequest
import com.normalnywork.tundramarket.data.remote.api.proto.CheckOrderStatusResponse
import com.normalnywork.tundramarket.data.remote.api.proto.CreateOrderResponse
import com.normalnywork.tundramarket.data.remote.api.proto.OrderListRequest
import com.normalnywork.tundramarket.data.remote.api.proto.OrderListResponse
import com.normalnywork.tundramarket.data.remote.api.proto.ProtoOrderCategory
import com.normalnywork.tundramarket.data.remote.api.schema.Order
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Singleton
import com.normalnywork.tundramarket.domain.entities.Order as DomainOrder

@Singleton
class KtorRemoteOrdersDataSource(
    private val httpClient: HttpClient,
) : RemoteOrdersDataSource {

    override suspend fun createOrder(
        order: DomainOrder,
        idempotencyKey: String,
    ): Int {
        val response = httpClient
            .post(Order.Create()) {
                contentType(ContentType.Application.ProtoBuf)
                header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                setBody(order.toCreateRequest())
            }
            .body<CreateOrderResponse>()

        return response.orderId
    }

    override suspend fun changeOrderStatus(
        orderId: Int,
        status: OrderStatus,
        idempotencyKey: String,
    ): Long {
        val response = httpClient
            .post(Order.ChangeStatus()) {
                contentType(ContentType.Application.ProtoBuf)
                header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                setBody(status.toChangeStatusRequest(orderId = orderId))
            }
            .body<ChangeOrderStatusResponse>()

        return response.time
    }

    override suspend fun checkCurrentOrderStatus(lastUpdated: Long): RemoteOrdersDataSource.OrderStatusUpdates {
        return httpClient
            .post(Order.CheckStatus()) {
                contentType(ContentType.Application.ProtoBuf)
                setBody(CheckOrderStatusRequest(lastUpdated = lastUpdated))
            }
            .body<CheckOrderStatusResponse>()
            .toOrderStatusUpdates()
    }

    override suspend fun getHistoryOrders(
        anchor: Int?,
        pageSize: Int,
    ): RemoteOrdersDataSource.OrderListPage {
        return httpClient
            .post(Order.List()) {
                contentType(ContentType.Application.ProtoBuf)
                setBody(
                    OrderListRequest(
                        anchor = anchor,
                        pageSize = pageSize,
                        orderCategory = ProtoOrderCategory.HISTORY,
                    ),
                )
            }
            .body<OrderListResponse>()
            .toOrderListPage()
    }

    private companion object {

        const val IDEMPOTENCY_KEY_HEADER = "Idempotency-Key"
    }
}
