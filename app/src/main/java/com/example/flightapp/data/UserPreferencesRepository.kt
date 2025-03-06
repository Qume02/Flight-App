package com.example.flightapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val IS_FAVORITES_GRID = booleanPreferencesKey("is_favorites_grid")
        val SEARCH_QUERY = stringPreferencesKey("search_query")
    }

    val isFavoritesGrid: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_FAVORITES_GRID] ?: false
        }

    suspend fun saveFavoritesGridPreferences(isGrid: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FAVORITES_GRID] = isGrid
        }
    }

    val searchQuery: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[SEARCH_QUERY] ?: ""
        }

    suspend fun saveSearchQuery(query: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_QUERY] = query
        }
    }
}