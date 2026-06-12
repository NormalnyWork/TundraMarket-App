package com.normalnywork.tundramarket.utils.sync

import com.normalnywork.tundramarket.domain.entities.UserRole
import com.normalnywork.tundramarket.domain.entities.isTerminal
import com.normalnywork.tundramarket.domain.usecases.auth.GetUserRoleUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.GetCurrentOrderUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Singleton

@Singleton
class CurrentOrderStatusUpdateEnqueuer(
    private val getUserRoleUseCase: GetUserRoleUseCase,
    private val getCurrentOrderUseCase: GetCurrentOrderUseCase,
    private val syncWorkScheduler: SyncWorkScheduler,
) {

    suspend fun enqueueCurrentOrderStatusUpdate() {
        if (getUserRoleUseCase() != UserRole.Nomad) {
            return
        }

        val currentOrder = getCurrentOrderUseCase().first()
        if (currentOrder?.status?.isTerminal != false) {
            return
        }

        syncWorkScheduler.schedule(syncCurrentOrderStatus = true)
    }
}
