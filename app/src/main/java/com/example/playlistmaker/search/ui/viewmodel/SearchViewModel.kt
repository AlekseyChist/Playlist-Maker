package com.example.playlistmaker.search.ui.viewmodel

import android.util.Log
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
        private const val SEARCH_DEBOUNCE_DELAY = 300L
        private const val TAG = "SearchViewModel"
    }

    init {
        Log.d(TAG, "ViewModel initialized")
        showHistory()
    }

    fun search(query: String) {
        Log.d(TAG, "search() called with query: '$query'")

        if (query.isBlank()) {
            Log.d(TAG, "Query is blank, showing history")
            showHistory()
            return
        }

        if (latestSearchText == query) {
            Log.d(TAG, "Query is the same as last search, ignoring")
            return
        }

        latestSearchText = query

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            Log.d(TAG, "Starting search after debounce delay")
            delay(SEARCH_DEBOUNCE_DELAY)

            Log.d(TAG, "Executing search use case for: '$query'")
            searchTracksUseCase.execute(query)
                .onStart {
                    Log.d(TAG, "Search started - showing Loading state")
                    _state.value = SearchState.Loading
                }
                .catch { error ->
                    Log.e(TAG, "Search error: ${error.message}", error)
                    _state.value = SearchState.Error(error.message ?: "Unknown error")
                }
                .collect { tracks ->
                    Log.d(TAG, "Search completed, found ${tracks.size} tracks")
                    _state.value = if (tracks.isEmpty()) {
                        SearchState.Empty
                    } else {
                        SearchState.Content(tracks)
                    }
                }
        }
    }

    fun showHistory() {
        Log.d(TAG, "showHistory() called")
        viewModelScope.launch {
            val history = searchHistoryUseCase.getHistory()
            Log.d(TAG, "History loaded: ${history.size} tracks")
            _state.value = SearchState.History(history)
        }
    }

    fun addToHistory(track: Track) {
        Log.d(TAG, "addToHistory() called for track: ${track.trackName}")
        viewModelScope.launch {
            searchHistoryUseCase.addTrack(track)
        }
    }

    fun clearHistory() {
        Log.d(TAG, "clearHistory() called")
        viewModelScope.launch {
            searchHistoryUseCase.clearHistory()
            _state.value = SearchState.History(emptyList())
        }
    }
}