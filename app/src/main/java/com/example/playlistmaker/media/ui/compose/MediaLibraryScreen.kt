package com.example.playlistmaker.media.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.compose.AppTopBar
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.viewmodel.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MediaLibraryScreen(
    favoriteTracksViewModel: FavoriteTracksViewModel,
    playlistsViewModel: PlaylistsViewModel,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    darkTheme: Boolean  // 🆕 Добавь этот параметр
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val tabs = listOf(
        stringResource(R.string.favorite_tracks),
        stringResource(R.string.playlists)
    )

    // 🆕 Оборачиваем в PlaylistMakerTheme
    PlaylistMakerTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.media)) }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Табы
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(title) }
                        )
                    }
                }

                // ViewPager с контентом
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> FavoriteTracksTab(
                            viewModel = favoriteTracksViewModel,
                            onTrackClick = onTrackClick
                        )
                        1 -> PlaylistsTab(
                            viewModel = playlistsViewModel,
                            onPlaylistClick = onPlaylistClick,
                            onCreatePlaylistClick = onCreatePlaylistClick
                        )
                    }
                }
            }
        }
    }
}