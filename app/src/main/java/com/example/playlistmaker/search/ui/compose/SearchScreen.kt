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
import com.example.playlistmaker.compose.AppTopBar
import com.example.playlistmaker.compose.Errors
import com.example.playlistmaker.compose.PlaceholderError
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.state.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.ui.theme.customEditTextFieldsColors

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit,
    darkTheme: Boolean
) {
    val state by viewModel.state.observeAsState(SearchState.History(emptyList()))
    var searchQuery by remember { mutableStateOf("") }

    PlaylistMakerTheme(darkTheme = darkTheme) {
        Scaffold(topBar = { AppTopBar(false, text = stringResource(R.string.search)) {} }) { pv ->
            Column(Modifier.fillMaxSize().padding(pv), horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.isEmpty()) viewModel.showHistory() else viewModel.search(it)
                    },
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.search)) },
                    colors = customEditTextFieldsColors(),
                )

                when (val s = state) {
                    is SearchState.Loading -> LoadingContent()
                    is SearchState.Content -> TracksList(
                        tracks = s.tracks,
                        onTrackClick = { track ->
                            viewModel.addToHistory(track)
                            onTrackClick(track)
                        }
                    )
                    is SearchState.Empty -> PlaceholderError(Errors.SearchNothingFound)
                    is SearchState.Error -> PlaceholderError(Errors.SearchNoConnection)
                    is SearchState.History -> {
                        if (s.tracks.isNotEmpty() && searchQuery.isEmpty()) {
                            SearchHistory(
                                tracks = s.tracks,
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