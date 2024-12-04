package com.example.playlistmaker.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.listeners.TracksConsumer
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

    fun search(query: String) {
        if (query.isBlank()) {
            showHistory()
            return
        }

        _state.value = SearchState.Loading
        searchTracksUseCase.execute(query, object : TracksConsumer {
            override fun consume(tracks: List<Track>) {
                _state.value = if (tracks.isEmpty()) {
                    SearchState.Empty
                } else {
                    SearchState.Content(tracks)
                }
            }
        })
    }

    fun showHistory() {
        val history = searchHistoryUseCase.getHistory()
        _state.value = SearchState.History(history)
    }

    fun getHistory(): List<Track> {
        return searchHistoryUseCase.getHistory()
    }

    fun addToHistory(track: Track) {
        searchHistoryUseCase.addTrack(track)
    }

    fun clearHistory() {
        searchHistoryUseCase.clearHistory()
        showHistory()
    }
}