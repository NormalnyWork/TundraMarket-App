package com.normalnywork.tundramarket.domain.entities

sealed interface OrderSmsSendState {

    data class Ready(val comment: String) : OrderSmsSendState

    data class CommentEditRequired(val comment: String) : OrderSmsSendState

    data object Unavailable : OrderSmsSendState
}

data class OrderSmsCommentState(
    val smsLength: Int?,
    val smsLimit: Int,
    val canSend: Boolean,
)
