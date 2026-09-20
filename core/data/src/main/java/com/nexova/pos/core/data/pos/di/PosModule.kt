package com.nexova.pos.core.data.pos.di

import com.nexova.pos.core.data.pos.PosDao
import com.nexova.pos.core.data.pos.PosRepository
import com.nexova.pos.core.data.sync.OfflineInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PosModule {
    @Provides
    @Singleton
    fun providePosRepository(
        posDao: PosDao,
        offlineInterceptor: OfflineInterceptor
    ): PosRepository {
        return PosRepository(posDao, offlineInterceptor)
    }
}
