package com.normalnywork.tundramarket.data.remote.api.schema

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("order")
class Order {

    @Serializable
    @Resource("create")
    class Create(val parent: Order = Order())

    @Serializable
    @Resource("change-status")
    class ChangeStatus(val parent: Order = Order())

    @Serializable
    @Resource("check-status")
    class CheckStatus(val parent: Order = Order())
}
