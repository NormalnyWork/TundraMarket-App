package com.normalnywork.tundramarket.domain.entities

import kotlinx.serialization.Serializable

@Serializable
enum class OrderNetworkStatus {
    Enqueued,
    Processing,
    Loading,
    Failed,
    LoadingSms,
    SmsFailed,
    Updating,
    UpdateFailed,
}
