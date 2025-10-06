package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.usecase.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.usecase.PlaylistInteractor
import com.example.playlistmaker.player.domain.service.AudioPlayerServiceController
import com.example.playlistmaker.player.ui.state.AudioPlayerState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _playlistAddStatus = MutableLiveData<PlaylistAddStatus>()
    val playlistAddStatus: LiveData<PlaylistAddStatus> = _playlistAddStatus

    private var playbackJob: Job? = null
    private var currentTrack: Track? = null
    private var serviceController: AudioPlayerServiceController? = null
    private var isUiInForeground = false

    init {
        _state.value = AudioPlayerState.Loading
    }

    fun setService(controller: AudioPlayerServiceController) {
        serviceController = controller

        // Устанавливаем слушатель завершения воспроизведения
        controller.setOnCompletionListener {
            stopPlaybackTimer()
            _state.value = AudioPlayerState.Prepared
            controller.hideNotification()
        }

        // Даем время на подготовку плеера
        viewModelScope.launch {
            delay(1000)
            if (_state.value is AudioPlayerState.Loading) {
                _state.value = AudioPlayerState.Prepared
            }
        }
    }

    fun setTrack(track: Track) {
        currentTrack = track
        checkIfTrackIsFavorite(track.trackId)
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists()
                .first()
                .also { _playlists.value = it }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        currentTrack?.let { track ->
            viewModelScope.launch {
                if (playlist.trackIds.contains(track.trackId)) {
                    _playlistAddStatus.value = PlaylistAddStatus.AlreadyExists(playlist.name)
                } else {
                    playlistInteractor.addTrackToPlaylist(track, playlist)
                    _playlistAddStatus.value = PlaylistAddStatus.Success(playlist.name)
                    loadPlaylists()
                }
            }
        }
    }

    private fun checkIfTrackIsFavorite(trackId: Long) {
        viewModelScope.launch {
            val isFav = favoriteTracksInteractor.isTrackFavorite(trackId)
            _isFavorite.value = isFav
            currentTrack?.isFavorite = isFav
        }
    }

    fun onFavoriteClicked() {
        currentTrack?.let { track ->
            viewModelScope.launch {
                if (track.isFavorite) {
                    favoriteTracksInteractor.removeTrackFromFavorites(track)
                    track.isFavorite = false
                    _isFavorite.value = false
                } else {
                    favoriteTracksInteractor.addTrackToFavorites(track)
                    track.isFavorite = true
                    _isFavorite.value = true
                }
            }
        }
    }

    fun play() {
        serviceController?.play()
        _state.value = AudioPlayerState.Playing(serviceController?.getCurrentPosition() ?: 0)
        startPlaybackTimer()
    }

    fun pause() {
        serviceController?.pause()
        _state.value = AudioPlayerState.Paused
        stopPlaybackTimer()

        // Скрываем уведомление если UI на переднем плане
        if (isUiInForeground) {
            serviceController?.hideNotification()
        }
    }

    fun onUiResumed() {
        isUiInForeground = true
        // Скрываем уведомление когда UI на переднем плане
        if (_state.value !is AudioPlayerState.Playing) {
            serviceController?.hideNotification()
        }
    }

    fun onUiPaused() {
        isUiInForeground = false
        // Показываем уведомление если воспроизводится музыка
        if (_state.value is AudioPlayerState.Playing) {
            serviceController?.showNotification()
        }
    }

    private fun startPlaybackTimer() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (serviceController?.isPlaying() == true) {
                delay(PLAYBACK_UPDATE_DELAY)
                _state.value = AudioPlayerState.Playing(serviceController?.getCurrentPosition() ?: 0)
            }
        }
    }

    private fun stopPlaybackTimer() {
        playbackJob?.cancel()
        playbackJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPlaybackTimer()
        serviceController?.hideNotification()
    }

    companion object {
        private const val PLAYBACK_UPDATE_DELAY = 300L
    }
}

sealed class PlaylistAddStatus {
    data class Success(val playlistName: String) : PlaylistAddStatus()
    data class AlreadyExists(val playlistName: String) : PlaylistAddStatus()
}