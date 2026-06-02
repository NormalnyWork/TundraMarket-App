package com.normalnywork.tundramarket.data.remote.source

import com.normalnywork.tundramarket.data.remote.api.mappers.toCreateRequest
import com.normalnywork.tundramarket.data.remote.api.proto.CreateOrderResponse
import com.normalnywork.tundramarket.data.remote.api.schema.Order
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

    private companion object {

        const val IDEMPOTENCY_KEY_HEADER = "Idempotency-Key"
    }
}
