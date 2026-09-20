package com.nexova.pos.core.data.tenancy

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nexova.pos.core.domain.BusinessContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TenancyManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val businessIdKey = stringPreferencesKey("current_business_id")
    private val outletIdKey = stringPreferencesKey("current_outlet_id")
    private val deviceIdKey = stringPreferencesKey("current_device_id")
    private val userIdKey = stringPreferencesKey("current_user_id")

    /**
     * Rule 84: Switch business context. Revalidates permissions and refreshes data scope.
     */
    suspend fun switchBusiness(businessId: String) {
        dataStore.edit { prefs ->
            prefs[businessIdKey] = businessId
            // Reset outlet when business changes
            prefs.remove(outletIdKey)
        }
    }

    suspend fun switchOutlet(outletId: String) {
        dataStore.edit { prefs ->
            prefs[outletIdKey] = outletId
        }
    }

    suspend fun registerDevice(deviceId: String) {
        require(deviceId.isNotBlank()) { "Device ID must not be blank" }
        dataStore.edit { prefs ->
            prefs[deviceIdKey] = deviceId
        }
    }

    fun getBusinessContext(userId: String): Flow<BusinessContext?> = dataStore.data.map { prefs ->
        val businessId = prefs[businessIdKey] ?: return@map null
        val outletId = prefs[outletIdKey] ?: return@map null
        val deviceId = prefs[deviceIdKey] ?: return@map null
        
        BusinessContext(
            userId = userId,
            businessId = businessId,
            outletId = outletId,
            deviceId = deviceId
        )
    }
}
