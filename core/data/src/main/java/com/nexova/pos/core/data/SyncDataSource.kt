package com.nexova.pos.core.data

interface SyncDataSource {
    suspend fun pull(): List<String> = emptyList()
    suspend fun push(payload: String): Boolean = true
}

object RemoteSyncDataSource : SyncDataSource {
    override suspend fun pull(): List<String> = emptyList()
    override suspend fun push(payload: String): Boolean = true
}
