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
        val result = processor.syncPendingOperations()

        return if (result.shouldRetry) {
            Result.retry()
        } else {
            Result.success()
        }
    }
}
