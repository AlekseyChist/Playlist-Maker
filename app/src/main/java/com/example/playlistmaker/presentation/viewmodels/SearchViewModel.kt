package com.example.playlistmaker.presentation.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.usecases.tracks.SearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.tracks.SearchTracksUseCase
import com.example.playlistmaker.presentation.state.SearchState

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val searchHistoryUseCase: SearchHistoryUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    fun search(query: String) {
        if (query.isBlank()) {
            showHistory()
            return
        }

        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = Runnable {
            _state.value = SearchState.Loading
            try {
                val result = searchTracksUseCase.execute(query)
                _state.value = if (result.isEmpty()) {
                    SearchState.Empty
                } else {
                    SearchState.Content(result)
                }
            } catch (e: Exception) {
                _state.value = SearchState.Error(e.message ?: "Unknown error")
            }
        }
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    fun showHistory() {
        val history = searchHistoryUseCase.getHistory()
        _state.value = if (history.isEmpty()) {
            SearchState.Content(emptyList())
        } else {
            SearchState.History(history)
        }
    }

    fun addToHistory(track: Track) {
        searchHistoryUseCase.addTrack(track)
    }

    fun clearHistory() {
        searchHistoryUseCase.clearHistory()
        _state.value = SearchState.Content(emptyList())
    }

    override fun onCleared() {
        super.onCleared()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}