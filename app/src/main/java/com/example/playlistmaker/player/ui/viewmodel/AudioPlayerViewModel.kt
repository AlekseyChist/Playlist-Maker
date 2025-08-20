package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.usecase.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.usecase.PlaylistInteractor
import com.example.playlistmaker.player.domain.usecase.AudioPlayerUseCase
import com.example.playlistmaker.player.ui.state.AudioPlayerState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val audioPlayerUseCase: AudioPlayerUseCase,
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

    init {
        _state.value = AudioPlayerState.Loading
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
                // Проверяем, есть ли трек уже в плейлисте
                if (playlist.trackIds.contains(track.trackId)) {
                    _playlistAddStatus.value = PlaylistAddStatus.AlreadyExists(playlist.name)
                } else {
                    // Добавляем трек в плейлист
                    playlistInteractor.addTrackToPlaylist(track, playlist)
                    _playlistAddStatus.value = PlaylistAddStatus.Success(playlist.name)
                    // Обновляем список плейлистов
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

    fun preparePlayer(url: String) {
        _state.value = AudioPlayerState.Loading
        try {
            // Подготавливаем плеер
            audioPlayerUseCase.preparePlayer(url)

            // Устанавливаем слушатель завершения воспроизведения
            audioPlayerUseCase.setOnCompletionListener {
                stopPlaybackTimer()
                _state.value = AudioPlayerState.Prepared
            }

            // Задержка для загрузки плеера
            viewModelScope.launch {
                delay(PREPARE_DELAY)
                if (_state.value is AudioPlayerState.Loading) {
                    _state.value = AudioPlayerState.Prepared
                }
            }
        } catch (e: Exception) {
            _state.value = AudioPlayerState.Error(e.message ?: "Unknown error")
        }
    }

    fun play() {
        try {
            audioPlayerUseCase.play()
            _state.value = AudioPlayerState.Playing(audioPlayerUseCase.getCurrentPosition())
            startPlaybackTimer()
        } catch (e: Exception) {
            _state.value = AudioPlayerState.Error(e.message ?: "Play error")
        }
    }

    fun pause() {
        try {
            audioPlayerUseCase.pause()
            _state.value = AudioPlayerState.Paused
            stopPlaybackTimer()
        } catch (e: Exception) {
            // Только логируем ошибку, не меняем состояние UI
        }
    }

    private fun startPlaybackTimer() {
        // Отменяем предыдущую корутину, если она есть
        playbackJob?.cancel()

        // Запускаем новую корутину для обновления прогресса
        playbackJob = viewModelScope.launch {
            while (audioPlayerUseCase.isPlaying()) {
                delay(PLAYBACK_UPDATE_DELAY)
                _state.value = AudioPlayerState.Playing(audioPlayerUseCase.getCurrentPosition())
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
        try {
            audioPlayerUseCase.release()
        } catch (e: Exception) {
            // Логирование ошибки, если нужно
        }
    }

    companion object {
        private const val PLAYBACK_UPDATE_DELAY = 300L
        private const val PREPARE_DELAY = 1000L
    }
}

sealed class PlaylistAddStatus {
    data class Success(val playlistName: String) : PlaylistAddStatus()
    data class AlreadyExists(val playlistName: String) : PlaylistAddStatus()
}