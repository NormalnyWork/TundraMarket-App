package com.normalnywork.tundramarket.data.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

const val DB_SYNC_OUTBOX_TABLE_NAME = "sync_outbox"
const val DB_SYNC_OUTBOX_COL_ID = "id"
const val DB_SYNC_OUTBOX_COL_OPERATION_TYPE = "operation_type"
const val DB_SYNC_OUTBOX_COL_LOCAL_ENTITY_ID = "local_entity_id"
const val DB_SYNC_OUTBOX_COL_STATUS = "status"
const val DB_SYNC_OUTBOX_COL_ATTEMPT_COUNT = "attempt_count"
const val DB_SYNC_OUTBOX_COL_NEXT_ATTEMPT_AT = "next_attempt_at"
const val DB_SYNC_OUTBOX_COL_CREATED_AT = "created_at"
const val DB_SYNC_OUTBOX_COL_IDEMPOTENCY_KEY = "idempotency_key"
const val DB_SYNC_OUTBOX_COL_LAST_ERROR = "last_error"
const val DB_SYNC_OUTBOX_COL_COMMENT = "comment"

@Entity(
    tableName = DB_SYNC_OUTBOX_TABLE_NAME,
    indices = [
        Index(DB_SYNC_OUTBOX_COL_OPERATION_TYPE),
        Index(DB_SYNC_OUTBOX_COL_LOCAL_ENTITY_ID),
        Index(DB_SYNC_OUTBOX_COL_STATUS),
        Index(DB_SYNC_OUTBOX_COL_NEXT_ATTEMPT_AT),
        Index(value = [DB_SYNC_OUTBOX_COL_IDEMPOTENCY_KEY], unique = true),
    ],
)
data class SyncOutboxEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DB_SYNC_OUTBOX_COL_ID)
    val id: Long = 0,
    @ColumnInfo(name = DB_SYNC_OUTBOX_COL_OPERATION_TYPE)
    val operationType: SyncOperationTypeEntity,
    @ColumnInfo(name = DB_SYNC_OUTBOX_COL_LOCAL_ENTITY_ID)
    val localEntityId: Int,
    @ColumnInfo(name = DB_SYNC_OUTBOX_COL_STATUS)
    val status: SyncOutboxStatusEntity = SyncOutboxStatusEntity.Pending,
    @ColumnInfo(name = DB_SYNC_OUTBOX_COL_ATTEMPT_COUNT)
    val attemptCount: Int = 0,
    @ColumnInfo(name = DB_SYNC_OUTBOX_COL_NEXT_ATTEMPT_AT)
    val nextAttemptAt: Long,
    @ColumnInfo(name = DB_SYNC_OUTBOX_COL_CREATED_AT)
    val createdAt: Long,
    @ColumnInfo(name = DB_SYNC_OUTBOX_COL_IDEMPOTENCY_KEY)
    val idempotencyKey: String,
    @ColumnInfo(name = DB_SYNC_OUTBOX_COL_LAST_ERROR)
    val lastError: String? = null,
    @ColumnInfo(name = DB_SYNC_OUTBOX_COL_COMMENT)
    val comment: String? = null,
)
