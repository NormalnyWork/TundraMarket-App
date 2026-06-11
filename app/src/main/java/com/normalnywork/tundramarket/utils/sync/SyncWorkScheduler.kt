package com.normalnywork.tundramarket.utils.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import org.koin.core.annotation.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class SyncWorkScheduler(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun schedule(
        replace: Boolean = false,
        syncCurrentOrderStatus: Boolean = false,
    ) {
        val existingWorkPolicy = if (replace) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.KEEP
        val immediateRequest = OneTimeWorkRequestBuilder<OutboxSyncWorker>()
            .setInputData(
                workDataOf(
                    OutboxSyncWorker.KEY_RETRY_WHEN_WAITING_FOR_NETWORK to false,
                    OutboxSyncWorker.KEY_SYNC_CURRENT_ORDER_STATUS to syncCurrentOrderStatus,
                ),
            )
            .build()
        val connectedRequest = OneTimeWorkRequestBuilder<OutboxSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .setInputData(
                workDataOf(
                    OutboxSyncWorker.KEY_RETRY_WHEN_WAITING_FOR_NETWORK to true,
                    OutboxSyncWorker.KEY_SYNC_CURRENT_ORDER_STATUS to syncCurrentOrderStatus,
                ),
            )
            .build()

        workManager.enqueueUniqueWork(
            IMMEDIATE_SYNC_WORK_NAME,
            existingWorkPolicy,
            immediateRequest,
        )
        workManager.enqueueUniqueWork(
            CONNECTED_SYNC_WORK_NAME,
            existingWorkPolicy,
            connectedRequest,
        )
    }

    fun scheduleWeeklyCatalogUpdates() {
        val request = PeriodicWorkRequestBuilder<WeeklyCatalogUpdateWorker>(
            repeatInterval = CATALOG_UPDATE_INTERVAL_DAYS,
            repeatIntervalTimeUnit = TimeUnit.DAYS,
        ).setInitialDelay(
            duration = CATALOG_UPDATE_INTERVAL_DAYS,
            timeUnit = TimeUnit.DAYS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            WEEKLY_CATALOG_UPDATE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    companion object {

        private const val IMMEDIATE_SYNC_WORK_NAME = "sync-outbox-immediate"
        private const val CONNECTED_SYNC_WORK_NAME = "sync-outbox-connected"
        private const val WEEKLY_CATALOG_UPDATE_WORK_NAME = "weekly-catalog-update"
        private const val CATALOG_UPDATE_INTERVAL_DAYS = 7L
    }
}
