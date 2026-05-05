package com.normalnywork.tundramarket.data.remote.source

import com.normalnywork.tundramarket.data.remote.api.proto.AuthRequest
import com.normalnywork.tundramarket.data.remote.api.proto.AuthResponse
import com.normalnywork.tundramarket.data.remote.api.schema.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Singleton

@Singleton
class KtorRemoteAuthDataSource(
    private val httpClient: HttpClient,
) : RemoteAuthDataSource {

    override suspend fun authorize(
        phoneNumber: String,
        tradingStationId: Int?,
    ): String {
        val response = httpClient
            .post(User.Auth()) {
                contentType(ContentType.Application.ProtoBuf)
                setBody(
                    AuthRequest(
                        phone = phoneNumber,
                        tradingStationId = tradingStationId,
                    ),
                )
            }
            .body<AuthResponse>()

        return response.token
    }
}
