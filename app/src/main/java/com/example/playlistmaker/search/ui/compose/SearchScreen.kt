package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.state.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.ui.theme.pmButtonColors
import com.example.playlistmaker.ui.theme.pmSearchFieldColors

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit,
    darkTheme: Boolean
) {
    val state by viewModel.state.observeAsState(SearchState.History(emptyList()))
    var query by remember { mutableStateOf("") }

    PlaylistMakerTheme(darkTheme = darkTheme) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { pv ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.search),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(16.dp))

                // Поле поиска (плейсхолдер 16sp, серый #AEAFB4)
                TextField(
                    value = query,
                    onValueChange = { text ->
                        query = text
                        if (text.isEmpty()) viewModel.showHistory() else viewModel.search(text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)           // стандартный M3 размер
                        .padding(bottom = 12.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = pmSearchFieldColors(),
                    textStyle = MaterialTheme.typography.bodyLarge,  // 16sp
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search),
                            style = MaterialTheme.typography.bodyLarge, // 16sp
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search_button),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                query = ""
                                viewModel.showHistory()
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.cancel_button),
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                when (val s = state) {
                    is SearchState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is SearchState.Content -> {
                        TracksList(
                            tracks = s.tracks,
                            onTrackClick = {
                                viewModel.addToHistory(it)
                                onTrackClick(it)
                            }
                        )
                    }

                    is SearchState.Empty -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.nothing_found),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    is SearchState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.connection_error),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    is SearchState.History -> {
                        HistoryBlock(
                            tracks = s.tracks,
                            visible = query.isEmpty(),
                            onTrackClick = {
                                viewModel.addToHistory(it)
                                onTrackClick(it)
                            },
                            onClear = { viewModel.clearHistory() }
                        )
                    }
                }
            }
        }
    }
}

/* -------- блок «История» -------- */

@Composable
private fun HistoryBlock(
    tracks: List<Track>,
    visible: Boolean,
    onTrackClick: (Track) -> Unit,
    onClear: () -> Unit
) {
    if (!visible || tracks.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp) // нижний общий отступ
    ) {
        Text(
            text = stringResource(R.string.search_history),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp) // 24dp от последнего трека до кнопки
        ) {
            items(tracks, key = { it.trackId }) { track ->
                TrackRow(track) { onTrackClick(track) }
            }
        }

        Button(
            onClick = onClear,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = pmButtonColors()
        ) {
            Text(
                text = stringResource(R.string.clear_history),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/* -------- списки -------- */

@Composable
private fun TracksList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(tracks, key = { it.trackId }) { track ->
            TrackRow(track) { onTrackClick(track) }
        }
    }
}

@Composable
private fun TrackRow(
    track: Track,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp) // расстояние между айтемами
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Обложка альбома — 45x45 dp, радиус 2 dp, отступ слева 13 dp
            AsyncImage(
                model = track.artworkUrl100,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 13.dp)         // 🔥 отступ от края экрана
                    .size(45.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Spacer(Modifier.width(12.dp)) // 🔥 расстояние между обложкой и текстом

            // Информация о треке
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp) // небольшой внутренний запас перед иконкой
            ) {
                Text(
                    text = track.trackName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${track.artistName} • ${formatDuration(track.trackTimeMillis)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Иконка справа (стрелка или меню)
            Icon(
                painter = painterResource(R.drawable.menu_dot_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

/* -------- utils -------- */

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}