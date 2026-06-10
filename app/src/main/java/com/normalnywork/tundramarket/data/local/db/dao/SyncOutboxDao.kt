package com.normalnywork.tundramarket.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.normalnywork.tundramarket.data.local.db.entities.DB_SYNC_OUTBOX_COL_ATTEMPT_COUNT
import com.normalnywork.tundramarket.data.local.db.entities.DB_SYNC_OUTBOX_COL_CREATED_AT
import com.normalnywork.tundramarket.data.local.db.entities.DB_SYNC_OUTBOX_COL_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_SYNC_OUTBOX_COL_LAST_ERROR
import com.normalnywork.tundramarket.data.local.db.entities.DB_SYNC_OUTBOX_COL_LOCAL_ENTITY_ID
import com.normalnywork.tundramarket.data.local.db.entities.DB_SYNC_OUTBOX_COL_NEXT_ATTEMPT_AT
import com.normalnywork.tundramarket.data.local.db.entities.DB_SYNC_OUTBOX_COL_STATUS
import com.normalnywork.tundramarket.data.local.db.entities.DB_SYNC_OUTBOX_TABLE_NAME
import com.normalnywork.tundramarket.data.local.db.entities.SyncOutboxEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOutboxStatusEntity

@Dao
interface SyncOutboxDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(operation: SyncOutboxEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(operation: SyncOutboxEntity)

    @Query(
        """
        SELECT * FROM $DB_SYNC_OUTBOX_TABLE_NAME
        WHERE $DB_SYNC_OUTBOX_COL_STATUS IN (:statuses)
            AND $DB_SYNC_OUTBOX_COL_NEXT_ATTEMPT_AT <= :now
        ORDER BY $DB_SYNC_OUTBOX_COL_CREATED_AT ASC, $DB_SYNC_OUTBOX_COL_ID ASC
        LIMIT 1
        """,
    )
    suspend fun getNextReadyOperation(
        now: Long,
        statuses: List<SyncOutboxStatusEntity>,
    ): SyncOutboxEntity?

    @Query(
        """
        SELECT * FROM $DB_SYNC_OUTBOX_TABLE_NAME
        WHERE $DB_SYNC_OUTBOX_COL_STATUS IN (:statuses)
            AND $DB_SYNC_OUTBOX_COL_NEXT_ATTEMPT_AT <= :now
        ORDER BY $DB_SYNC_OUTBOX_COL_CREATED_AT ASC, $DB_SYNC_OUTBOX_COL_ID ASC
        """,
    )
    suspend fun getReadyOperations(
        now: Long,
        statuses: List<SyncOutboxStatusEntity>,
    ): List<SyncOutboxEntity>

    @Query(
        """
        SELECT * FROM $DB_SYNC_OUTBOX_TABLE_NAME
        WHERE $DB_SYNC_OUTBOX_COL_STATUS = :status
        ORDER BY $DB_SYNC_OUTBOX_COL_CREATED_AT ASC, $DB_SYNC_OUTBOX_COL_ID ASC
        """,
    )
    suspend fun getRunningOperations(
        status: SyncOutboxStatusEntity = SyncOutboxStatusEntity.Running,
    ): List<SyncOutboxEntity>

    @Query(
        """
        SELECT COUNT(*) FROM $DB_SYNC_OUTBOX_TABLE_NAME
        WHERE $DB_SYNC_OUTBOX_COL_STATUS IN (:statuses)
        """,
    )
    suspend fun getOperationCount(statuses: List<SyncOutboxStatusEntity>): Int

    @Query(
        """
        UPDATE $DB_SYNC_OUTBOX_TABLE_NAME
        SET $DB_SYNC_OUTBOX_COL_STATUS = :status
        WHERE $DB_SYNC_OUTBOX_COL_ID = :operationId
        """,
    )
    suspend fun updateStatus(
        operationId: Long,
        status: SyncOutboxStatusEntity,
    )

    @Query(
        """
        UPDATE $DB_SYNC_OUTBOX_TABLE_NAME
        SET $DB_SYNC_OUTBOX_COL_STATUS = :status,
            $DB_SYNC_OUTBOX_COL_ATTEMPT_COUNT = $DB_SYNC_OUTBOX_COL_ATTEMPT_COUNT + 1,
            $DB_SYNC_OUTBOX_COL_NEXT_ATTEMPT_AT = :nextAttemptAt,
            $DB_SYNC_OUTBOX_COL_LAST_ERROR = :lastError
        WHERE $DB_SYNC_OUTBOX_COL_ID = :operationId
        """,
    )
    suspend fun markFailed(
        operationId: Long,
        status: SyncOutboxStatusEntity = SyncOutboxStatusEntity.Failed,
        nextAttemptAt: Long,
        lastError: String?,
    )

    @Query(
        """
        UPDATE $DB_SYNC_OUTBOX_TABLE_NAME
        SET $DB_SYNC_OUTBOX_COL_STATUS = :pendingStatus
        WHERE $DB_SYNC_OUTBOX_COL_STATUS = :runningStatus
        """,
    )
    suspend fun resetRunningOperations(
        pendingStatus: SyncOutboxStatusEntity = SyncOutboxStatusEntity.Pending,
        runningStatus: SyncOutboxStatusEntity = SyncOutboxStatusEntity.Running,
    )

    @Query(
        """
        DELETE FROM $DB_SYNC_OUTBOX_TABLE_NAME
        WHERE $DB_SYNC_OUTBOX_COL_LOCAL_ENTITY_ID = :localEntityId
        """,
    )
    suspend fun deleteByLocalEntityId(localEntityId: Int)

    @Delete
    suspend fun delete(operation: SyncOutboxEntity)
}
