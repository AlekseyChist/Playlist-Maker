package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.state.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit,
    darkTheme: Boolean
) {
    val state by viewModel.state.observeAsState(SearchState.History(emptyList()))
    var searchQuery by remember { mutableStateOf("") }

    PlaylistMakerTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                SearchTopBar()
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Поле поиска с обработчиком onSearch
                SearchTextField(
                    value = searchQuery,
                    onValueChange = { newValue ->
                        searchQuery = newValue
                        viewModel.search(newValue)
                    },
                    onClearClick = {
                        searchQuery = ""
                        viewModel.showHistory()
                    },
                    onSearch = { query ->
                        // Обработка нажатия на поиск на клавиатуре
                        viewModel.search(query)
                    },
                    modifier = Modifier.padding(16.dp)
                )

                // Контент в зависимости от состояния
                when (val currentState = state) {
                    is SearchState.Loading -> {
                        LoadingContent()
                    }
                    is SearchState.Content -> {
                        TracksList(
                            tracks = currentState.tracks,
                            onTrackClick = { track ->
                                viewModel.addToHistory(track)
                                onTrackClick(track)
                            }
                        )
                    }
                    is SearchState.Empty -> {
                        EmptySearchPlaceholder()
                    }
                    is SearchState.Error -> {
                        ErrorPlaceholder(
                            onRefreshClick = { viewModel.search(searchQuery) }
                        )
                    }
                    is SearchState.History -> {
                        if (currentState.tracks.isNotEmpty() && searchQuery.isEmpty()) {
                            SearchHistory(
                                tracks = currentState.tracks,
                                onTrackClick = { track ->
                                    viewModel.addToHistory(track)
                                    onTrackClick(track)
                                },
                                onClearHistory = { viewModel.clearHistory() }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Добавляем отсутствующую функцию SearchTopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.search),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

// Остальные функции остаются без изменений
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun TracksList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = tracks,
            key = { it.trackId }
        ) { track ->
            TrackItem(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }
    }
}

@Composable
private fun SearchHistory(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = stringResource(R.string.search_history),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
            )
        }

        items(
            items = tracks,
            key = { it.trackId }
        ) { track ->
            TrackItem(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }

        item {
            Button(
                onClick = onClearHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.clear_history))
            }
        }
    }
}