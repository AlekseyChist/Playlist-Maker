package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.clickable
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
    darkTheme: Boolean  // 🆕 Добавь этот параметр
) {
    val state by viewModel.state.observeAsState(SearchState.History(emptyList()))
    var searchQuery by remember { mutableStateOf("") }

    // 🆕 Оборачиваем в PlaylistMakerTheme
    PlaylistMakerTheme(darkTheme = darkTheme) {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Поисковая строка
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.isEmpty()) {
                            viewModel.showHistory()
                        } else {
                            viewModel.search(it)
                        }
                    },
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.search)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Контент в зависимости от состояния
                when (val currentState = state) {
                    is SearchState.Loading -> LoadingContent()
                    is SearchState.Content -> TracksList(
                        tracks = currentState.tracks,
                        onTrackClick = { track ->
                            viewModel.addToHistory(track)
                            onTrackClick(track)
                        }
                    )
                    is SearchState.Empty -> EmptyContent()
                    is SearchState.Error -> ErrorContent()
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
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.nothing_found),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ErrorContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.connection_error),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
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

// TrackItem - composable для отображения трека
@Composable
private fun TrackItem(
    track: Track,
    onClick: () -> Unit
) {
    // Реализация элемента списка трека
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}