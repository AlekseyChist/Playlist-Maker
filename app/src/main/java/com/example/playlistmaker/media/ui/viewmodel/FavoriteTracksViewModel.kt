package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.usecase.FavoriteTracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val interactor: FavoriteTracksInteractor
) : ViewModel() {

    private val _favoriteTracksState = MutableStateFlow<List<Track>>(emptyList())
    val favoriteTracksState: StateFlow<List<Track>> = _favoriteTracksState.asStateFlow()

    init {
        loadFavoriteTracks()
    }

    private fun loadFavoriteTracks() {
        viewModelScope.launch {
            interactor.getFavoriteTracks().collect { tracks ->
                _favoriteTracksState.value = tracks
            }
        }
    }

    fun addToFavorites(track: Track) {
        viewModelScope.launch {
            interactor.addTrackToFavorites(track)
        }
    }

    fun removeFromFavorites(track: Track) {
        viewModelScope.launch {
            interactor.removeTrackFromFavorites(track)
        }
    }
}