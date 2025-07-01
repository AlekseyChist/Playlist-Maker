package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.usecase.FavoriteTracksInteractor
import com.example.playlistmaker.media.ui.state.FavoriteTracksState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val interactor: FavoriteTracksInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoriteTracksState>()
    val state: LiveData<FavoriteTracksState> = _state

    init {
        loadFavoriteTracks()
    }

    private fun loadFavoriteTracks() {
        viewModelScope.launch {
            interactor.getFavoriteTracks().collect { tracks ->
                if (tracks.isEmpty()) {
                    _state.value = FavoriteTracksState.Empty
                } else {
                    _state.value = FavoriteTracksState.Content(tracks)
                }
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