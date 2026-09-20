package com.nexova.pos.core.data.sync.di

import com.nexova.pos.core.data.SyncOperationDao
import com.nexova.pos.core.data.sync.SyncRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {
    @Provides
    @Singleton
    fun provideSyncRepository(syncOperationDao: SyncOperationDao): SyncRepository {
        return SyncRepository(syncOperationDao)
    }
}
