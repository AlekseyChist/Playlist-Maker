package com.example.playlistmaker.media.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.playlistmaker.R
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
    darkTheme: Boolean
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf(
        stringResource(R.string.favorite_tracks),
        stringResource(R.string.playlists)
    )

    PlaylistMakerTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                MediaLibraryTopBar()
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Табы с правильными цветами для светлой и темной темы
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    divider = {}, // Убираем разделитель
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            // Черный в светлой теме, белый в темной
                            color = if (darkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1B22)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    // Выбранный таб: черный в светлой, белый в темной
                                    color = if (pagerState.currentPage == index) {
                                        if (darkTheme) Color(0xFFFFFFFF) else Color(0xFF1A1B22)
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        )
                    }
                }

                // Контент вкладок
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaLibraryTopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.media),
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}