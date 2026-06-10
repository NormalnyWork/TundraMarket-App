package com.normalnywork.tundramarket.data.remote.source

import com.normalnywork.tundramarket.data.remote.api.mappers.toChangeStatusRequest
import com.normalnywork.tundramarket.data.remote.api.mappers.toCreateRequest
import com.normalnywork.tundramarket.data.remote.api.mappers.toOrderListItem
import com.normalnywork.tundramarket.data.remote.api.mappers.toOrderListPage
import com.normalnywork.tundramarket.data.remote.api.mappers.toOrderStatusUpdates
import com.normalnywork.tundramarket.data.remote.api.mappers.toProto
import com.normalnywork.tundramarket.data.remote.api.mappers.unixMillisecondsToSeconds
import com.normalnywork.tundramarket.data.remote.api.mappers.unixSecondsToMilliseconds
import com.normalnywork.tundramarket.data.remote.api.proto.ChangeOrderStatusResponse
import com.normalnywork.tundramarket.data.remote.api.proto.CheckOrderStatusRequest
import com.normalnywork.tundramarket.data.remote.api.proto.CheckOrderStatusResponse
import com.normalnywork.tundramarket.data.remote.api.proto.CreateOrderResponse
import com.normalnywork.tundramarket.data.remote.api.proto.OrderCreateForNomadIn
import com.normalnywork.tundramarket.data.remote.api.proto.OrderListRequest
import com.normalnywork.tundramarket.data.remote.api.proto.OrderListResponse
import com.normalnywork.tundramarket.data.remote.api.proto.OrderResponse
import com.normalnywork.tundramarket.data.remote.api.proto.OrderUpdatesRequest
import com.normalnywork.tundramarket.data.remote.api.proto.ProtoOrderCategory
import com.normalnywork.tundramarket.data.remote.api.proto.ProtoProductCount
import com.normalnywork.tundramarket.data.remote.api.schema.Order
import com.normalnywork.tundramarket.data.remote.api.schema.User
import com.normalnywork.tundramarket.domain.entities.Location
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.TradingStationOrdersPage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
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

    override suspend fun createOrderForNomad(
        nomadPhone: String,
        location: Location,
        products: List<RemoteOrdersDataSource.ProductCount>,
        comment: String?,
        idempotencyKey: String,
    ): Int {
        val response = httpClient
            .post(Order.CreateForNomad()) {
                contentType(ContentType.Application.ProtoBuf)
                header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                setBody(
                    OrderCreateForNomadIn(
                        nomadPhone = nomadPhone,
                        location = listOf(location.toProto()),
                        products = products.map { product ->
                            ProtoProductCount(
                                productId = product.productId,
                                count = product.count,
                            )
                        },
                        comment = comment,
                    ),
                )
            }
            .body<CreateOrderResponse>()

        return response.orderId
    }

    override suspend fun changeOrderStatus(
        orderId: Int,
        status: OrderStatus,
        idempotencyKey: String,
        comment: String?,
    ): Long {
        val response = httpClient
            .post(Order.ChangeStatus()) {
                contentType(ContentType.Application.ProtoBuf)
                header(IDEMPOTENCY_KEY_HEADER, idempotencyKey)
                setBody(
                    status.toChangeStatusRequest(
                        orderId = orderId,
                        comment = comment,
                    ),
                )
            }
            .body<ChangeOrderStatusResponse>()

        return response.time.unixSecondsToMilliseconds()
    }

    override suspend fun checkCurrentOrderStatus(lastUpdated: Long): RemoteOrdersDataSource.OrderStatusUpdates {
        return httpClient
            .post(Order.CheckStatus()) {
                contentType(ContentType.Application.ProtoBuf)
                setBody(
                    CheckOrderStatusRequest(
                        lastUpdated = lastUpdated.unixMillisecondsToSeconds(),
                    ),
                )
            }
            .body<CheckOrderStatusResponse>()
            .toOrderStatusUpdates()
    }

    override suspend fun getCurrentOrder(): RemoteOrdersDataSource.OrderListItem? {
        return httpClient
            .get(User.CurrentOrder())
            .body<OrderResponse>()
            .toOrderListItem()
    }

    override suspend fun getHistoryOrders(
        anchor: Int?,
        pageSize: Int,
    ): RemoteOrdersDataSource.OrderListPage {
        return getOrders(
            anchor = anchor,
            pageSize = pageSize,
            orderCategory = ProtoOrderCategory.HISTORY,
        )
    }

    override suspend fun getTradingStationOrders(
        page: TradingStationOrdersPage,
        anchor: Int?,
        pageSize: Int,
    ): RemoteOrdersDataSource.OrderListPage {
        return getOrders(
            anchor = anchor,
            pageSize = pageSize,
            orderCategory = page.toOrderCategory(),
        )
    }

    override suspend fun getOrderUpdates(lastUpdated: Long): RemoteOrdersDataSource.OrderListPage {
        return httpClient
            .post(Order.Updates()) {
                contentType(ContentType.Application.ProtoBuf)
                setBody(
                    OrderUpdatesRequest(
                        lastUpdated = lastUpdated.unixMillisecondsToSeconds(),
                    ),
                )
            }
            .body<OrderListResponse>()
            .toOrderListPage()
    }

    private suspend fun getOrders(
        anchor: Int?,
        pageSize: Int,
        orderCategory: ProtoOrderCategory,
    ): RemoteOrdersDataSource.OrderListPage {
        return httpClient
            .post(Order.List()) {
                contentType(ContentType.Application.ProtoBuf)
                setBody(
                    OrderListRequest(
                        anchor = anchor,
                        pageSize = pageSize,
                        orderCategory = orderCategory,
                    ),
                )
            }
            .body<OrderListResponse>()
            .toOrderListPage()
    }

    private fun TradingStationOrdersPage.toOrderCategory() = when (this) {
        TradingStationOrdersPage.Active -> ProtoOrderCategory.PROCESSING
        TradingStationOrdersPage.New -> ProtoOrderCategory.NEW
        TradingStationOrdersPage.History -> ProtoOrderCategory.HISTORY
    }

    private companion object {

        const val IDEMPOTENCY_KEY_HEADER = "Idempotency-Key"
    }
}
