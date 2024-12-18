package com.example.playlistmaker.search.ui.state

import com.example.playlistmaker.search.domain.model.Track

sealed class SearchState {
    object Loading : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    object Empty : SearchState()
    data class Error(val message: String) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
}