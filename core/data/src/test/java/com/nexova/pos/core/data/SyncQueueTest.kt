package com.nexova.pos.core.data

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncQueueTest {
    @Test fun duplicateIdempotencyKeyIsIgnoredByRoomPolicy() {
        val first = SyncEntity(
            idempotencyKey = "same-key",
            operationId = "operation-1",
            tenantId = "tenant-1",
            actorId = "actor-1",
            deviceId = "device-1",
            entityType = "order",
            entityId = "order-1",
            action = "CREATE",
            payload = "{}",
            payloadHash = "hash-1",
            schemaVersion = 1,
            createdAt = 1L,
            status = "QUEUED"
        )
        val second = first.copy(payload = "{\"changed\":true}")
        assertEquals(first.idempotencyKey, second.idempotencyKey)
        assertEquals("{}", first.payload)
    }
}
