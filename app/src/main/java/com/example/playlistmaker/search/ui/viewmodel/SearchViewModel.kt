package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.listener.TracksConsumer
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.usecase.SearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.search.ui.state.SearchState

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