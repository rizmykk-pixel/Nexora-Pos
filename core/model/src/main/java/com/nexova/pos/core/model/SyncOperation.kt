package com.nexova.pos.core.model

import com.nexova.pos.core.common.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class SyncOperation(
    val operationId: String = UUID.randomUUID().toString(),
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant = Instant.now(),
    val payload: String,
    val status: SyncStatus = SyncStatus.QUEUED,
    val retryCount: Int = 0,
    val correlationId: String? = null
)

enum class SyncStatus { QUEUED, PROCESSING, SUCCEEDED, FAILED_RETRYABLE, FAILED_PERMANENT, CONFLICT, CANCELLED }
