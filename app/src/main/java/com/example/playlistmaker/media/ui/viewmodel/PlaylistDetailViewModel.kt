// app/src/main/java/com/example/playlistmaker/media/ui/viewmodel/PlaylistDetailViewModel.kt

package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.usecase.PlaylistInteractor
import com.example.playlistmaker.media.ui.state.PlaylistDetailState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.utils.Event
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistDetailState>()
    val state: LiveData<PlaylistDetailState> = _state

    // Меняем на Event wrapper для одноразовых событий
    private val _shareEvent = MutableLiveData<Event<String>>()
    val shareEvent: LiveData<Event<String>> = _shareEvent

    private val _showEmptyPlaylistMessage = MutableLiveData<Event<Boolean>>()
    val showEmptyPlaylistMessage: LiveData<Event<Boolean>> = _showEmptyPlaylistMessage

    private val _playlistDeleted = MutableLiveData<Event<Boolean>>()
    val playlistDeleted: LiveData<Event<Boolean>> = _playlistDeleted

    private var currentPlaylistId: Long = 0

    fun loadPlaylist(playlistId: Long) {
        currentPlaylistId = playlistId
        _state.value = PlaylistDetailState.Loading

        viewModelScope.launch {
            try {
                val playlist = playlistInteractor.getPlaylistById(playlistId)

                if (playlist == null) {
                    _state.value = PlaylistDetailState.Error
                    return@launch
                }

                val tracks = playlistInteractor.getPlaylistTracks(playlist.trackIds)
                val totalDuration = calculateTotalDuration(tracks)

                _state.value = PlaylistDetailState.Content(
                    playlist = playlist,
                    tracks = tracks,
                    totalDuration = totalDuration
                )
            } catch (e: Exception) {
                _state.value = PlaylistDetailState.Error
            }
        }
    }

    fun removeTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            try {
                playlistInteractor.removeTrackFromPlaylist(currentPlaylistId, track.trackId)
                loadPlaylist(currentPlaylistId) // Перезагружаем данные
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
    }

    fun sharePlaylist() {
        val currentState = _state.value
        if (currentState is PlaylistDetailState.Content) {
            if (currentState.tracks.isEmpty()) {
                // Оборачиваем в Event
                _showEmptyPlaylistMessage.value = Event(true)
            } else {
                val shareText = playlistInteractor.generateShareText(
                    currentState.playlist,
                    currentState.tracks
                )
                // Оборачиваем в Event
                _shareEvent.value = Event(shareText)
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            try {
                playlistInteractor.deletePlaylist(currentPlaylistId)
                // Оборачиваем в Event
                _playlistDeleted.value = Event(true)
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
    }

    private fun calculateTotalDuration(tracks: List<Track>): String {
        val totalMillis = tracks.sumOf { it.trackTimeMillis }
        val totalMinutes = totalMillis / 60000
        return "$totalMinutes минут"
    }
}