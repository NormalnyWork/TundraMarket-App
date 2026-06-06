package com.normalnywork.tundramarket.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.context.GlobalContext

class OutboxSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val processor = GlobalContext.get().get<OutboxSyncProcessor>()
        val result = processor.syncPendingOperations(
            retryWhenWaitingForNetwork = inputData.getBoolean(
                KEY_RETRY_WHEN_WAITING_FOR_NETWORK,
                false,
            ),
            syncCurrentOrderStatus = inputData.getBoolean(
                KEY_SYNC_CURRENT_ORDER_STATUS,
                false,
            ),
        )

        return if (result.shouldRetry) {
            Result.retry()
        } else {
            Result.success()
        }
    }

    companion object {

        const val KEY_RETRY_WHEN_WAITING_FOR_NETWORK = "retry_when_waiting_for_network"
        const val KEY_SYNC_CURRENT_ORDER_STATUS = "sync_current_order_status"
    }
}
