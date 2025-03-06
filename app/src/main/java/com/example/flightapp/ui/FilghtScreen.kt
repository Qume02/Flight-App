package com.example.flightapp.ui


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightapp.R
import com.example.flightapp.data.Airport
import com.example.flightapp.data.Favorite
import kotlinx.coroutines.launch

@Composable
fun FlightApp(
    flightSearchViewModel: FlightSearchViewModel = viewModel(
        factory = FlightSearchViewModel.Factory
    )
) {
    val uiState by flightSearchViewModel.uiState.collectAsState()
    FlightSearch(uiState = uiState, flightSearchViewModel = flightSearchViewModel)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearch(
    uiState: FlightUiState,
    flightSearchViewModel: FlightSearchViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) } // Corrected
    ) { innerPadding ->
        MainScreen(
            uiState = uiState,
            flightSearchViewModel = flightSearchViewModel,
            snackbarHostState = snackbarHostState,
            Modifier.padding(innerPadding)
        )
    }
}

/**
 * Main screen of the app.
 */
@Composable
fun MainScreen(
    uiState: FlightUiState,
    flightSearchViewModel: FlightSearchViewModel,
    snackbarHostState: SnackbarHostState, // <---- RECEIVE SnackbarHostState as Parameter
    modifier: Modifier = Modifier
    ) {
    Column(modifier = modifier) {
        SearchField(
            searchText = uiState.searchText,
            onSearchTextChange = { flightSearchViewModel.onSearchTextChange(it) }
        )
        ContentDisplay(
            uiState = uiState,
            flightSearchViewModel = flightSearchViewModel,
            snackbarHostState = snackbarHostState
        )
    }
}

/**
 * Shows the search field.
 */
@Composable
fun SearchField(searchText: String, onSearchTextChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = searchText,
        onValueChange = { newText ->
            Log.d("SearchField", "onValueChange - New Text: $newText")
            onSearchTextChange(newText)
        },
        placeholder =  { stringResource(R.string.search_placeholder) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
        trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Close") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.padding_medium), vertical = dimensionResource(R.dimen.padding_small)),
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black, // Text color when focused
            unfocusedTextColor = Color.Black, // Text color when unfocused
            focusedPlaceholderColor = Color.Gray, // Placeholder color when focused
            unfocusedPlaceholderColor = Color.Gray, // Placeholder color when unfocused
        )
    )
}

/**
 * Shows the content of the app.
 */
@Composable
fun ContentDisplay(
    uiState: FlightUiState,
    flightSearchViewModel: FlightSearchViewModel,
    snackbarHostState: SnackbarHostState
) {
    Log.d("ContentDisplay", "searchText: ${uiState.searchText}")
    Log.d("ContentDisplay", "airports: ${uiState.airports.size}")
    Log.d("ContentDisplay", "selectedAirport: ${uiState.selectedAirport}")
    Log.d("ContentDisplay", "airportsSelected: ${uiState.airportsSelected}")

    when {
        // case 1: When we chose from the suggested airport and the destination is not empty
        uiState.selectedAirport != null && uiState.destinations.isNotEmpty() -> {
            FlightDestinations(
                destinations = uiState.destinations,
                selectedAirport = uiState.selectedAirport,
                flightSearchViewModel = flightSearchViewModel,
                snackbarHostState = snackbarHostState
            )
            Log.d("ContentDisplay", "Branch: Destinations")
            Log.d("ContentDisplay", "selectedAirport: ${uiState.selectedAirport}")
            Log.d("ContentDisplay", "destinations.size: ${uiState.destinations.size}")
        }
        // case 2: when we put some text in the textField and get some options to chose
        uiState.searchText.isNotEmpty() && uiState.airports.isNotEmpty() -> {
            Log.d("ContentDisplay", "Branch: Autocomplete")
            Log.d("ContentDisplay", "searchText: ${uiState.searchText}")
            Log.d("ContentDisplay", "airports.size: ${uiState.airports.size}")
            AutocompleteSuggestions(
                airports = uiState.airports,
                onAirportSelected = { flightSearchViewModel.onAirportSelected(it)}
            )
        }
        // case 3: when we put nothing in the textField and get the favorite destination that we have chose before.
        uiState.searchText.isEmpty() && uiState.favorites.isNotEmpty() -> {
            Log.d("ContentDisplay", "Branch: Favorites")
            Log.d("ContentDisplay", "favorites.size: ${uiState.favorites.size}")
            FavoriteRoutes(
                favorites = uiState.favorites,
                flightSearchViewModel = flightSearchViewModel,
                snackbarHostState = snackbarHostState
            )
        }
        // case 4: when we put nothing in the textField but we don't have any favorite destinations. We will show some error message.
        uiState.searchText.isEmpty() && uiState.favorites.isEmpty() -> {
            Log.d("ContentDisplay", "Branch: Empty")
            EmptyMessage(errorMessage = stringResource(R.string.no_favorites_error_message))
        }
    }
}

/**
 * Case 2: Shows the list of airports that match the search query or similar to that.
 */
@Composable
fun AutocompleteSuggestions(
    airports: List<Airport>,
    onAirportSelected: (Airport) -> Unit
) {
    Log.d("AutocompleteSuggestions", "Composing AutocompleteSuggestions with ${airports.size} airports") // <-- ADD THIS LOG

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.padding_medium), vertical = dimensionResource(R.dimen.padding_small))
            .wrapContentHeight()
    ) {
        Log.d("AutocompleteSuggestions", "LazyColumn items block is being executed")
        Log.d("AutocompleteSuggestions", "Airports list inside LazyColumn: $airports")

        items(
            items = airports,
            key = { airport -> airport.id }) { airport ->
            Log.d("AutocompleteSuggestions-Item", "Rendering item: ${airport.iataCode}, ${airport.name}") // <-- ADD THIS LOG - for EACH item

            val annotatedString = AnnotatedString.Builder().apply {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(airport.iataCode)
                }
                append(" ")
                append(airport.name)
            }.toAnnotatedString()
            Text(text = annotatedString, modifier = Modifier
                .fillMaxWidth()
                .clickable { onAirportSelected(airport) })

        }
        Log.d("AutocompleteSuggestions", "LazyColumn items block execution finished") // <-- ADD THIS LOG
    }
    Log.d("AutocompleteSuggestions", "Finished composing AutocompleteSuggestions") // <-- ADD THIS LOG
}

/**
 * Case 3: Shows the list of favorite destinations.
 */
@Composable
fun FavoriteRoutes(
    favorites: List<Favorite>,
    flightSearchViewModel: FlightSearchViewModel,
    snackbarHostState: SnackbarHostState
) {
    Text(text = stringResource(R.string.favorite_routes), fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
    LazyColumn {
        items(
            count = favorites.size,
            key = { index -> "${favorites[index].departureCode}-${favorites[index].destinationCode}" }
        ) { index ->
            val favorite = favorites[index]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.depart), fontWeight = FontWeight.SemiBold, color = Color.Gray)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = favorite.departureCode, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                var departureAirportName by remember { mutableStateOf<String?>(null) }
                                LaunchedEffect(favorite.departureCode) {
                                    departureAirportName = flightSearchViewModel.getAirportsByIataCode(favorite.departureCode)?.name
                                }
                                departureAirportName?.let {
                                    Text(text = it, fontSize = 14.sp)
                                }
                            }
                        }
                        FavoriteButton(
                            flightSearchViewModel = flightSearchViewModel,
                            departureCode = favorite.departureCode,
                            destinationCode = favorite.destinationCode,
                            snackbarHostState = snackbarHostState,
                            onToggleFavorite = { departure, destination ->
                                flightSearchViewModel.toggleFavorite(departure, destination)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.arrive), fontWeight = FontWeight.SemiBold, color = Color.Gray)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = favorite.destinationCode, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))

                                var destinationAirportName by remember { mutableStateOf<String?>(null) }
                                LaunchedEffect(favorite.destinationCode) {
                                    destinationAirportName = flightSearchViewModel.getAirportsByIataCode(favorite.destinationCode)?.name
                                }
                                destinationAirportName?.let {
                                    Text(text = it, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Case 1: Shows the list of destinations.
 */
@Composable
fun FlightDestinations(
    destinations: List<Airport>,
    selectedAirport: Airport,
    flightSearchViewModel: FlightSearchViewModel,
    snackbarHostState: SnackbarHostState
) {
    Log.d("FlightDestinations", "Composing with destinations list size: ${destinations.size}") // <--- LOG SIZE HERE
    Log.d("FlightDestinations", "Destinations list: $destinations") // <--- LOG LIST CONTENT (if not too large)

    Text(text = "${stringResource(R.string.flights_from)} ${selectedAirport.iataCode}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
    LazyColumn {
        items(destinations) { destination ->
            Log.d("FlightDestinations-Item", "Rendering item: ${destination.iataCode}") // <--- LOG EACH ITEM
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.depart), fontWeight = FontWeight.SemiBold, color = Color.Gray)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = selectedAirport.iataCode, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp)) // Adds a small space between iataCode and Name
                                Text(text = selectedAirport.name, fontWeight = FontWeight.Normal, fontSize = 14.sp)
                            }
                        }
                        FavoriteButton(
                            flightSearchViewModel = flightSearchViewModel,
                            departureCode = selectedAirport.iataCode,
                            destinationCode = destination.iataCode,
                            onToggleFavorite = { departure, destination ->
                                flightSearchViewModel.toggleFavorite(departure, destination)
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.arrive), fontWeight = FontWeight.SemiBold, color = Color.Gray)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = destination.iataCode, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp)) // Adds a small space between iataCode and Name
                                Text(text = destination.name, fontWeight = FontWeight.Normal, fontSize = 14.sp)
                            }
                          }
                    }
                }
            }
        }
    }
}

/**
 * Case 4: Shows an empty message.
 */
@Composable
fun EmptyMessage(errorMessage: String) {
    Text(text = errorMessage, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}


@Composable
fun FavoriteButton(
    departureCode: String,
    destinationCode: String,
    onToggleFavorite: (String, String) -> Unit,
    flightSearchViewModel: FlightSearchViewModel,
    snackbarHostState: SnackbarHostState
) {
    var isFavoriteState by remember {
        mutableStateOf(flightSearchViewModel.isFavorite(departureCode, destinationCode))
    }

//    var showDialog by remember { mutableStateOf(false) } // state to control dialog visibility
//    var dialogMessage by remember { mutableStateOf("") } // state to hold dialog message

    val scope = rememberCoroutineScope()

    val addedToFavoritesMessage = stringResource(R.string.added_to_favorites_dialog_message, "$departureCode - $destinationCode")
    val removedFromFavoritesMessage = stringResource(R.string.removed_from_favorites_dialog_message, "$departureCode - $destinationCode")

    IconButton(onClick = {
        val willbeFavorite = !isFavoriteState
        onToggleFavorite(departureCode, destinationCode)
        isFavoriteState = !isFavoriteState

        scope.launch {
            if (willbeFavorite) {
                snackbarHostState.showSnackbar(
                    message = addedToFavoritesMessage
                )
            } else {
                snackbarHostState.showSnackbar(
                    message = removedFromFavoritesMessage
                )
            }
        }


    }) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = if (isFavoriteState) "Remove from favorites" else "Add to favorites",
            tint = if (isFavoriteState) Color.Yellow else Color.Black
        )
    }
}