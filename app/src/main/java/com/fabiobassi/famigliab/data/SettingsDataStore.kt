package com.fabiobassi.famigliab.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(context: Context) {

    private val dataStore = context.dataStore

    fun getColorFor(person: String): Flow<String> {
        val colorKey = stringPreferencesKey("${person.lowercase()}_color")
        return dataStore.data.map { preferences ->
            preferences[colorKey] ?: ""
        }
    }

    suspend fun setColorFor(person: String, color: String) {
        val colorKey = stringPreferencesKey("${person.lowercase()}_color")
        dataStore.edit { settings ->
            settings[colorKey] = color
        }
    }
}
