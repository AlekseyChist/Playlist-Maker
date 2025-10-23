package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.compose.AppTopBar
import com.example.playlistmaker.compose.Errors
import com.example.playlistmaker.compose.PlaceholderError
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.state.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.ui.theme.pmButtonColors

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit,
    onBackClick: () -> Unit = {},
    darkTheme: Boolean
) {
    val state by viewModel.state.observeAsState(SearchState.History(emptyList()))
    var searchQuery by remember { mutableStateOf("") }

    PlaylistMakerTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                // Кастомный TopBar с стрелкой назад
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Стрелка назад
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.back_button_vector),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Заголовок "Поиск"
                        Text(
                            text = stringResource(R.string.search),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 22.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily(
                                    androidx.compose.ui.text.font.Font(R.font.ys_text_medium)
                                )
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Используем кастомный SearchTextField
                SearchTextField(
                    value = searchQuery,
                    onValueChange = { newValue ->
                        searchQuery = newValue
                        if (newValue.isEmpty()) {
                            viewModel.showHistory()
                        } else {
                            viewModel.search(newValue)
                        }
                    },
                    onClearClick = {
                        searchQuery = ""
                        viewModel.showHistory()
                    },
                    onSearch = { query ->
                        viewModel.search(query)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Отображение состояний
                when (val currentState = state) {
                    is SearchState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is SearchState.Content -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(currentState.tracks) { track ->
                                TrackItem(
                                    track = track,
                                    onClick = {
                                        viewModel.addToHistory(track)
                                        onTrackClick(track)
                                    }
                                )
                            }
                        }
                    }

                    is SearchState.Empty -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            PlaceholderError(error = Errors.SearchNothingFound)
                        }
                    }

                    is SearchState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            PlaceholderError(error = Errors.SearchNoConnection)

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { viewModel.search(searchQuery) }
                            ) {
                                Text(stringResource(R.string.refresh))
                            }
                        }
                    }

                    is SearchState.History -> {
                        if (currentState.tracks.isNotEmpty() && searchQuery.isEmpty()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                // Заголовок "Вы искали"
                                item {
                                    Text(
                                        text = stringResource(R.string.search_history),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp)
                                            .wrapContentWidth(Alignment.CenterHorizontally)
                                    )
                                }

                                // Список треков
                                items(currentState.tracks) { track ->
                                    TrackItem(
                                        track = track,
                                        onClick = {
                                            viewModel.addToHistory(track)
                                            onTrackClick(track)
                                        }
                                    )
                                }

                                // Отступ 24dp перед кнопкой
                                item {
                                    Spacer(modifier = Modifier.height(24.dp))
                                }

                                // Кнопка "Очистить историю"
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Button(
                                            onClick = { viewModel.clearHistory() },
                                            modifier = Modifier
                                                .wrapContentWidth() // Hug content
                                                .height(36.dp),
                                            shape = RoundedCornerShape(54.dp),
                                            colors = pmButtonColors(),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.clear_history),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}