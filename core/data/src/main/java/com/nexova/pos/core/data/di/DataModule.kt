package com.nexova.pos.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.nexova.pos.core.data.NexovaDatabase
import com.nexova.pos.core.data.SyncOperationDao
import com.nexova.pos.core.data.catalog.CatalogDao
import com.nexova.pos.core.data.pos.PosDao
import com.nexova.pos.core.data.inventory.InventoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NexovaDatabase {
        return NexovaDatabase.create(context)
    }

    @Provides
    fun provideSyncOperationDao(database: NexovaDatabase): SyncOperationDao {
        return database.syncOperationDao()
    }

    @Provides
    fun provideCatalogDao(database: NexovaDatabase): CatalogDao {
        return database.catalogDao()
    }

    @Provides
    fun providePosDao(database: NexovaDatabase): PosDao {
        return database.posDao()
    }

    @Provides
    fun provideInventoryDao(database: NexovaDatabase): InventoryDao {
        return database.inventoryDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("nexova_prefs") }
        )
    }
}
