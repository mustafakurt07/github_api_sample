package com.example.fordtask2.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    companion object {
        val VIEW_TYPE_KEY = intPreferencesKey("view_type")
    }

    // Grid türünü kaydet
    suspend fun saveViewType(viewType: Int) {
        context.dataStore.edit { preferences ->
            preferences[VIEW_TYPE_KEY] = viewType
        }
    }

    // Grid türünü oku
    val viewTypeFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[VIEW_TYPE_KEY] ?: 1 // Varsayılan değer 1
        }
}