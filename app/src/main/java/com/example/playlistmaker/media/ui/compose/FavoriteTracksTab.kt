package com.example.playlistmaker.media.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.state.FavoriteTracksState
import com.example.playlistmaker.media.ui.viewmodel.FavoriteTracksViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.compose.TrackItem

@Composable
fun FavoriteTracksTab(
    viewModel: FavoriteTracksViewModel,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.observeAsState(FavoriteTracksState.Empty)

    when (val currentState = state) {
        is FavoriteTracksState.Empty -> {
            EmptyFavoriteTracksPlaceholder(modifier)
        }
        is FavoriteTracksState.Content -> {
            FavoriteTracksList(
                tracks = currentState.tracks,
                onTrackClick = onTrackClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun FavoriteTracksList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
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
private fun EmptyFavoriteTracksPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Image(
                painter = painterResource(R.drawable.nothing_found),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.your_library_is_empty),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}