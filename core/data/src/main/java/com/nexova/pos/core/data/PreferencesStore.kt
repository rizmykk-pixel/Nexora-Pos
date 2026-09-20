package com.nexova.pos.core.data

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.preferencesDataStore by preferencesDataStore(name = "nexova_preferences")

class PreferencesStore(private val context: Context) {
    private val outletKey = stringPreferencesKey("selected_outlet")
    val selectedOutlet = context.preferencesDataStore.data.map { it[outletKey] ?: "Main Outlet" }
}
