package com.normalnywork.tundramarket.data.remote.api.schema

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("user")
class User {

    @Serializable
    @Resource("auth")
    class Auth(val parent: User = User())

    @Serializable
    @Resource("catalog")
    class Catalog(val parent: User = User())
}