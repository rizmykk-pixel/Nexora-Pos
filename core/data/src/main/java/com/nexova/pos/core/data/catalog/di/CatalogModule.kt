package com.nexova.pos.core.data.catalog.di

import com.nexova.pos.core.data.catalog.CatalogDao
import com.nexova.pos.core.data.catalog.CatalogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CatalogModule {
    @Provides
    @Singleton
    fun provideCatalogRepository(catalogDao: CatalogDao): CatalogRepository {
        return CatalogRepository(catalogDao)
    }
}
