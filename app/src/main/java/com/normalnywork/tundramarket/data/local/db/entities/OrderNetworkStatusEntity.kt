package com.normalnywork.tundramarket.data.local.db.entities

enum class OrderNetworkStatusEntity {
    Enqueued,
    Processing,
    Loading,
    Failed,
    LoadingSms,
    SmsFailed,
    Updating,
    UpdateFailed,
}
