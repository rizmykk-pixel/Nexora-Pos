package com.nexova.pos.core.data.sync

import com.nexova.pos.core.data.SyncEntity
import com.nexova.pos.core.data.SyncOperationDao
import com.nexova.pos.core.domain.SyncOperation
import com.nexova.pos.core.domain.SyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val syncOperationDao: SyncOperationDao
) {
    fun observeSyncQueue(): Flow<List<SyncOperation>> {
        return syncOperationDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun enqueue(operation: SyncOperation, metadata: SyncMetadata) {
        syncOperationDao.enqueue(operation.toEntity(metadata))
    }

    suspend fun pushPending() {
        val now = System.currentTimeMillis()
        val pending = syncOperationDao.getPending(now)
        if (pending.isEmpty()) return

        // Implement actual Supabase push logic here
        // Rule 147: Send queued operations batches
    }

    private fun SyncEntity.toDomain() = SyncOperation(
        idempotencyKey = idempotencyKey,
        createdAt = Instant.ofEpochMilli(createdAt),
        payload = payload,
        state = SyncState.valueOf(status),
        retryCount = retryCount,
        correlationId = correlationId
    )

    private fun SyncOperation.toEntity(meta: SyncMetadata) = SyncEntity(
        idempotencyKey = idempotencyKey,
        operationId = idempotencyKey, // Defaulting to key for now
        tenantId = meta.tenantId,
        actorId = meta.actorId,
        deviceId = meta.deviceId,
        entityType = meta.entityType,
        entityId = meta.entityId,
        action = meta.action,
        payload = payload,
        payloadHash = payload.hashCode().toString(),
        schemaVersion = 1,
        createdAt = createdAt.toEpochMilli(),
        status = state.name,
        retryCount = retryCount,
        correlationId = correlationId
    )
}

data class SyncMetadata(
    val tenantId: String,
    val actorId: String,
    val deviceId: String,
    val entityType: String,
    val entityId: String,
    val action: String
)
