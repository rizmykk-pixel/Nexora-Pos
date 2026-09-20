package com.nexova.pos.core.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val authTokenKey = stringPreferencesKey("auth_token")
    private val userIdKey = stringPreferencesKey("user_id")

    val authToken: Flow<String?> = dataStore.data.map { it[authTokenKey] }
    val userId: Flow<String?> = dataStore.data.map { it[userIdKey] }

    suspend fun saveSession(token: String, userId: String) {
        dataStore.edit { preferences ->
            preferences[authTokenKey] = token
            preferences[userIdKey] = userId
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(authTokenKey)
            preferences.remove(userIdKey)
        }
    }
}
