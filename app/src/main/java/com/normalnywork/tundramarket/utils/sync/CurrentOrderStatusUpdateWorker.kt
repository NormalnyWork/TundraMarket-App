package com.normalnywork.tundramarket.utils.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.context.GlobalContext

class CurrentOrderStatusUpdateWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return runCatching {
            GlobalContext.get().get<CurrentOrderStatusUpdateEnqueuer>()
                .enqueueCurrentOrderStatusUpdate()
        }.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() },
        )
    }
}
