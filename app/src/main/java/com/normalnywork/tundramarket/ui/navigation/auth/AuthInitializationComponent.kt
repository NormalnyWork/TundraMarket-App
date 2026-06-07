package com.normalnywork.tundramarket.ui.navigation.auth

import com.arkivanov.decompose.ComponentContext
import com.normalnywork.tundramarket.domain.entities.UserRole
import kotlinx.coroutines.flow.StateFlow

interface AuthInitializationComponent {

    val auth: StateFlow<Status>

    val tradingStations: StateFlow<Status?>
    val catalog: StateFlow<Status?>
    val currentOrder: StateFlow<Status?>

    enum class Status {
        Queued,
        Processing,
        Done,
    }

    fun interface Factory {

        operator fun invoke(
            componentContext: ComponentContext,
            role: UserRole,
            phoneNumber: String,
            tradingStationId: Int?,
            proceed: () -> Unit,
        ): AuthInitializationComponent
    }
}
