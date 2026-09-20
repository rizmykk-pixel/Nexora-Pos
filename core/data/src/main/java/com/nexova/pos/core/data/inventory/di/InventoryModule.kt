package com.nexova.pos.core.data.inventory.di

import com.nexova.pos.core.data.inventory.InventoryDao
import com.nexova.pos.core.data.inventory.InventoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InventoryModule {
    @Provides
    @Singleton
    fun provideInventoryRepository(inventoryDao: InventoryDao): InventoryRepository {
        return InventoryRepository(inventoryDao)
    }
}
