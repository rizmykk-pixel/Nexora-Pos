package com.nexova.pos.core.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Rule 129: Offline Queue Fields.
 */
@Entity(
    tableName = "sync_operations",
    indices = [
        Index(value = ["status"]),
        Index(value = ["tenantId"]),
        Index(value = ["nextRetryAt"])
    ]
)
data class SyncEntity(
    @PrimaryKey val idempotencyKey: String,
    val operationId: String,
    val tenantId: String,
    val actorId: String,
    val deviceId: String,
    val entityType: String,
    val entityId: String,
    val action: String,
    val payload: String,
    val payloadHash: String,
    val schemaVersion: Int,
    val createdAt: Long,
    val status: String, // Matches SyncState names persisted by SyncRepository.
    val retryCount: Int = 0,
    val nextRetryAt: Long = 0,
    val lastError: String? = null,
    val correlationId: String? = null,
    val causationId: String? = null
)

@Dao
interface SyncOperationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun enqueue(operation: SyncEntity): Long

    @Query("SELECT * FROM sync_operations WHERE idempotencyKey = :key")
    suspend fun getByKey(key: String): SyncEntity?

    @Query("SELECT * FROM sync_operations ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<SyncEntity>>

    @Query("SELECT * FROM sync_operations WHERE status IN ('PENDING', 'FAILED') AND nextRetryAt <= :now ORDER BY createdAt ASC")
    suspend fun getPending(now: Long): List<SyncEntity>

    @Query("UPDATE sync_operations SET status = :status, retryCount = :retryCount, nextRetryAt = :nextRetry, lastError = :error WHERE idempotencyKey = :key")
    suspend fun updateStatus(key: String, status: String, retryCount: Int, nextRetry: Long, error: String?): Int

    @Query("DELETE FROM sync_operations WHERE status = 'SUCCEEDED' AND createdAt < :timestamp")
    suspend fun purgeOldSucceeded(timestamp: Long): Int
}
