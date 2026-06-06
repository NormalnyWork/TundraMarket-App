package com.normalnywork.tundramarket.domain.entities

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
