package com.normalnywork.tundramarket.data.remote.api.schema

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("trading-stations")
class TradingStations {

    @Serializable
    @Resource("list")
    class List(val parent: TradingStations = TradingStations())
}