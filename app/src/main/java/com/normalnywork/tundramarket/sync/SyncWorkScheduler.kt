package com.normalnywork.tundramarket.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.koin.core.annotation.Singleton

@Singleton
class SyncWorkScheduler(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun schedule(replace: Boolean = false) {
        val request = OneTimeWorkRequestBuilder<OutboxSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .build()

        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            if (replace) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.KEEP,
            request,
        )
    }

    companion object {

        private const val SYNC_WORK_NAME = "sync-outbox"
    }
}
