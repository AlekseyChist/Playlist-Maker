package com.example.playlistmaker.player.ui.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.usecase.AudioPlayerUseCase
import com.example.playlistmaker.player.ui.state.AudioPlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val audioPlayerUseCase: AudioPlayerUseCase
) : ViewModel() {

    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    private var playbackJob: Job? = null

    init {
        _state.value = AudioPlayerState.Loading
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