package com.normalnywork.tundramarket.data.remote.api

import com.normalnywork.tundramarket.data.remote.api.auth.AuthTokenStore
import com.normalnywork.tundramarket.utils.TMConst
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.koin.core.annotation.Singleton
import java.util.concurrent.TimeUnit

@Singleton
@OptIn(ExperimentalSerializationApi::class)
fun provideNetworkClient(
    tokenStore: AuthTokenStore,
) = HttpClient(OkHttp) {
    expectSuccess = true

    defaultRequest {
        url(TMConst.API_ENDPOINT)
    }

    install(ContentNegotiation) {
        protobuf(
            ProtoBuf { encodeDefaults = false }
        )
    }
    install(Auth) {
        bearer {
            loadTokens {
                tokenStore.getToken()?.let {
                    BearerTokens(
                        accessToken = it,
                        refreshToken = null,
                    )
                }
            }
        }
    }
    install(Resources)

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println(message)
            }
        }
        level = LogLevel.ALL
    }

    engine {
        config {
            retryOnConnectionFailure(true)
            connectTimeout(0, TimeUnit.SECONDS)
        }
    }
}
