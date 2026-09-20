package com.nexova.pos.core.data.sync

import com.nexova.pos.core.data.session.SessionManager
import com.nexova.pos.core.data.tenancy.TenancyManager
import com.nexova.pos.core.domain.SyncOperation
import com.nexova.pos.core.domain.SyncState
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineInterceptor @Inject constructor(
    private val syncRepository: SyncRepository,
    private val sessionManager: SessionManager,
    private val tenancyManager: TenancyManager
) {
    /**
     * Rule 126: Offline support is a first-class feature.
     * Intercepts mutations and queues them for synchronization.
     */
    suspend fun interceptMutation(
        entityType: String,
        entityId: String,
        action: String,
        payload: String
    ) {
        val userId = sessionManager.userId.first() ?: return
        val context = tenancyManager.getBusinessContext(userId).first() ?: return
        
        val metadata = SyncMetadata(
            tenantId = context.businessId,
            actorId = userId,
            deviceId = context.deviceId,
            entityType = entityType,
            entityId = entityId,
            action = action
        )

        val operation = SyncOperation(
            payload = payload,
            state = SyncState.PENDING,
            createdAt = Instant.now()
        )

        syncRepository.enqueue(operation, metadata)
    }
}
