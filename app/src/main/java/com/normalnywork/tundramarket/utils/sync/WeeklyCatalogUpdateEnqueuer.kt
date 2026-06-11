package com.normalnywork.tundramarket.utils.sync

import com.normalnywork.tundramarket.data.local.db.dao.SyncOutboxDao
import com.normalnywork.tundramarket.data.local.db.entities.SyncOperationTypeEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOutboxEntity
import org.koin.core.annotation.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class WeeklyCatalogUpdateEnqueuer(
    private val syncOutboxDao: SyncOutboxDao,
    private val syncWorkScheduler: SyncWorkScheduler,
) {

    suspend fun enqueueWeeklyUpdate() {
        val now = System.currentTimeMillis()
        val week = TimeUnit.MILLISECONDS.toDays(now) / DAYS_IN_WEEK

        enqueueOperation(
            operationType = SyncOperationTypeEntity.UpdateCatalog,
            now = now,
            week = week,
        )
        enqueueOperation(
            operationType = SyncOperationTypeEntity.UpdateTradingStations,
            now = now,
            week = week,
        )
        syncWorkScheduler.schedule()
    }

    private suspend fun enqueueOperation(
        operationType: SyncOperationTypeEntity,
        now: Long,
        week: Long,
    ) {
        syncOutboxDao.insertIgnore(
            SyncOutboxEntity(
                operationType = operationType,
                localEntityId = SYSTEM_CATALOG_UPDATE_ENTITY_ID,
                nextAttemptAt = now,
                createdAt = now,
                idempotencyKey = "${operationType.name}-$week",
            ),
        )
    }

    private companion object {

        const val DAYS_IN_WEEK = 7L
        const val SYSTEM_CATALOG_UPDATE_ENTITY_ID = 0
    }
}
