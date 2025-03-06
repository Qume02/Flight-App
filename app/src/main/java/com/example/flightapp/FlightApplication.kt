package com.example.flightapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.flightapp.data.FlightDatabase
import com.example.flightapp.data.UserPreferencesRepository

private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)

class FlightApplication: Application() {
    lateinit var database: FlightDatabase
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        database = FlightDatabase.getDatabase(this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}