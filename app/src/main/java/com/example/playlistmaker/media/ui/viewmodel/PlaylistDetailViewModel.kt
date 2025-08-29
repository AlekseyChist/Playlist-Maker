package com.example.playlistmaker.media.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.usecase.PlaylistInteractor
import com.example.playlistmaker.media.ui.state.PlaylistDetailState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistDetailState>()
    val state: LiveData<PlaylistDetailState> = _state

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
                // Перезагружаем плейлист после удаления
                loadPlaylist(currentPlaylistId)
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

    fun formatTracksCount(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "$count трек"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "$count трека"
            else -> "$count треков"
        }
    }
}