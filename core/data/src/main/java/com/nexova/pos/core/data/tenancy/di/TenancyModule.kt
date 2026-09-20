package com.nexova.pos.core.data.tenancy.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nexova.pos.core.data.tenancy.TenancyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TenancyModule {
    @Provides
    @Singleton
    fun provideTenancyManager(dataStore: DataStore<Preferences>): TenancyManager {
        return TenancyManager(dataStore)
    }
}
