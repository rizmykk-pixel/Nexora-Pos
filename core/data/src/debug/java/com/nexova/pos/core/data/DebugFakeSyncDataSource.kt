package com.nexova.pos.core.data

class DebugFakeSyncDataSource : SyncDataSource {
    override suspend fun pull(): List<String> = emptyList()
    override suspend fun push(payload: String): Boolean = true
}
