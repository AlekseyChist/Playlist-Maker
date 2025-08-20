// app/src/main/java/com/example/playlistmaker/media/ui/viewmodel/PlaylistsViewModel.kt
package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.usecase.PlaylistInteractor
import com.example.playlistmaker.media.ui.state.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsState>()
    val state: LiveData<PlaylistsState> = _state

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    _state.value = PlaylistsState.Empty
                } else {
                    _state.value = PlaylistsState.Content(playlists)
                }
            }
        }
    }
}