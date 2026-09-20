package com.nexova.pos.core.data.sync.di

import com.nexova.pos.core.data.session.SessionManager
import com.nexova.pos.core.data.tenancy.TenancyManager
import com.nexova.pos.core.data.sync.OfflineInterceptor
import com.nexova.pos.core.data.sync.SyncRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OfflineModule {
    @Provides
    @Singleton
    fun provideOfflineInterceptor(
        syncRepository: SyncRepository,
        sessionManager: SessionManager,
        tenancyManager: TenancyManager
    ): OfflineInterceptor {
        return OfflineInterceptor(syncRepository, sessionManager, tenancyManager)
    }
}
