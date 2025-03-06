package com.example.flightapp.ui

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightapp.FlightApplication
import com.example.flightapp.data.Airport
import com.example.flightapp.data.AirportDao
import com.example.flightapp.data.Favorite
import com.example.flightapp.data.FavoriteDao
import com.example.flightapp.data.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlightSearchViewModel(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlightUiState())

    val uiState: StateFlow<FlightUiState> = combine(
        _uiState,
        userPreferencesRepository.isFavoritesGrid
    ) { uiState, isFavoritesGrid ->
        uiState.copy(isFavoritesGrid = isFavoritesGrid)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FlightUiState()
    )

    init {
        Log.d("ViewModel", "INIT BLOCK STARTED") // <--- ADD INIT BLOCK START LOG
        viewModelScope.launch {
            userPreferencesRepository.searchQuery.collect { savedQuery ->
                Log.d("ViewModel", "COLLECTING searchQuery FLOW, received query: $savedQuery") // Keep this log
                _uiState.update { it.copy(searchText = savedQuery) }
                loadData(savedQuery)
            }
        }
        loadData("") // <--- FORCE INITIAL LOAD DATA CALL WITH EMPTY QUERY
        Log.d("ViewModel", "Forced loadData('') call in init") // Add a log for this forced call
        Log.d("ViewModel", "INIT BLOCK ENDED") // Keep this log
    }

    private fun loadData(query: String) {
        Log.d("ViewModel", "LOAD DATA ENTRY - Query: $query, AirportsSelected: ${_uiState.value.airportsSelected}") // <--- MODIFIED LOAD DATA ENTRY LOG
        Log.d("ViewModel", "loadData - Query before DAO call: $query, airportsSelected: ${_uiState.value.airportsSelected}") // <----  ADD THIS MISSING LOG HERE!  VERY IMPORTANT!
        viewModelScope.launch {
            // Only load airports or favorites if no airports is selected.
            if (!_uiState.value.airportsSelected) {
                if (query.isNotEmpty()) {
                    airportDao.getAirportsByNameOrIata(query).collect { airports ->
                        Log.d("ViewModel", "Airports loaded: ${airports.size}")
                        _uiState.update { it.copy(airports = airports) }
                    }
                } else {
                    favoriteDao.getAllFavorites().collect { favorites ->
                        Log.d("ViewModel", "Favorites loaded: ${favorites.size}")
                        _uiState.update { it.copy(favorites = favorites, favoriteRoutes = favorites.associate { Pair(it.departureCode, it.destinationCode) to true }.toMutableMap()) }                    }
                }
            }
        }
        Log.d("ViewModel", "LOAD DATA EXIT - Query: $query") // <--- ADD LOAD DATA EXIT LOG
    }

    fun onSearchTextChange(text: String) {
        Log.d("ViewModel", "onSearchTextChange: text = $text") // <--- ADD LOG IN onSearchTextChange
        _uiState.update { it.copy(airportsSelected = false, selectedAirport = null, destinations = emptyList(), searchText = text) }
        loadData(text)
        if (text.isEmpty()) {
            loadData("") // <--- Call loadData("") when text is empty
        }
    }

    fun onAirportSelected(airport: Airport) {
        viewModelScope.launch {
            airportDao.getDestinationsByDeparture().collect { destinations ->
                Log.d("ViewModel", "Selected Airport: ${airport.iataCode}")
                Log.d("ViewModel", "Destinations: ${destinations.size}")
                _uiState.update { it.copy(
                    destinations = destinations,
                    selectedAirport = airport,
                    airportsSelected = true,
                    searchText = airport.iataCode
                )}
            }
        }
    }

    fun clearSelectedAirport() {
        _uiState.update { it.copy(selectedAirport = null, destinations = emptyList()) }
    }

    fun isFavorite(departureCode: String, destinationCode: String): Boolean {
        return _uiState.value.favoriteRoutes.getOrDefault(Pair(departureCode, destinationCode), false)
    }

    fun toggleFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isCurrentlyFavorite = withContext(Dispatchers.IO) {
                favoriteDao.getFavoriteByCodes(departureCode, destinationCode) != null
            }
            if (isCurrentlyFavorite) {
                favoriteDao.deleteFavoriteByCodes(departureCode, destinationCode)
                _uiState.update { uiState ->
                    uiState.copy(
                        favoriteRoutes = uiState.favoriteRoutes.toMutableMap().apply {
                            remove(Pair(departureCode, destinationCode))
                        }
                    )
                }
            } else {
                favoriteDao.insertFavorite(Favorite(departureCode = departureCode, destinationCode = destinationCode))
                _uiState.update { uiState ->
                    uiState.copy(
                        favoriteRoutes = uiState.favoriteRoutes.toMutableMap().apply {
                            put(Pair(departureCode, destinationCode), true)
                        }
                    )
                }
            }
        }
    }


    suspend fun getAirportsByIataCode(iataCode: String): Airport? {
        return withContext(Dispatchers.IO) {
            airportDao.getAirportByIataCode(iataCode)
        }
    }

    companion object {
        private val SEARCH_QUERY = stringPreferencesKey("search_query")
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightApplication)
                FlightSearchViewModel(
                    application.database.airportDao(),
                    application.database.favoriteDao(),
                    application.userPreferencesRepository)
            }
        }
    }
}

data class FlightUiState(
    val searchText: String = "",
    val airports: List<Airport> = emptyList(),
    val destinations: List<Airport> = emptyList(),
    val favorites: List<Favorite> = emptyList(),
    val selectedAirport: Airport? = null,
    val isFavoritesGrid: Boolean = false,
    val airportsSelected: Boolean = false,
    val favoriteRoutes: MutableMap<Pair<String, String>, Boolean> = mutableMapOf(),
    val recompositionKey: Int = 0
)