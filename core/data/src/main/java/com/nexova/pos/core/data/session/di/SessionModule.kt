package com.nexova.pos.core.data.session.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nexova.pos.core.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {
    @Provides
    @Singleton
    fun provideSessionManager(dataStore: DataStore<Preferences>): SessionManager {
        return SessionManager(dataStore)
    }
}
