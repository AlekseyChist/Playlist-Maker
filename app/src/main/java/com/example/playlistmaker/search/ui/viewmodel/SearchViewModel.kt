package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.usecase.SearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.search.ui.state.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val searchHistoryUseCase: SearchHistoryUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private var latestSearchText: String? = null
    private var searchJob: Job? = null

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    fun search(query: String) {
        if (query.isBlank()) {
            showHistory()
            return
        }

        if (latestSearchText == query) {
            return
        }

        latestSearchText = query

        // Отменяем предыдущий поиск если он был
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)

            searchTracksUseCase.execute(query)
                .onStart { _state.value = SearchState.Loading }
                .catch { error ->
                    _state.value = SearchState.Error(error.message ?: "Unknown error")
                }
                .collect { tracks ->
                    _state.value = if (tracks.isEmpty()) {
                        SearchState.Empty
                    } else {
                        SearchState.Content(tracks)
                    }
                }
        }
    }

    fun showHistory() {
        viewModelScope.launch {
            val history = searchHistoryUseCase.getHistory()
            if (history.isEmpty()) {
                return@launch
            }
            _state.value = SearchState.History(history)
        }
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryUseCase.addTrack(track)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryUseCase.clearHistory() // suspend вызов
            _state.value = SearchState.History(emptyList())
        }
    }
}