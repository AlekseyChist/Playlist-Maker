package com.example.playlistmaker.player.ui.viewmodel

import android.content.ContentValues.TAG
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore.Audio
import android.util.Log
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

            // Задержка через корутину вместо Handler
            viewModelScope.launch {
                delay(PREPARE_DELAY)
                if (_state.value is AudioPlayerState.Loading) {
                    _state.value = AudioPlayerState.Prepared
                }
            }
        } catch (e: Exception) {
            _state.value = AudioPlayerState.Error(e.message ?: "Unknown error")
            Log.e(TAG, "Error preparing player", e)
        }
    }

    fun play() {
        try {
            audioPlayerUseCase.play()
            _state.value = AudioPlayerState.Playing(audioPlayerUseCase.getCurrentPosition())
            startPlaybackTimer()
        } catch (e: Exception) {
            _state.value = AudioPlayerState.Error(e.message ?: "Play error")
            Log.e(TAG, "Error playing", e)
        }
    }

    fun pause() {
        try {
            audioPlayerUseCase.pause()
            _state.value = AudioPlayerState.Paused
            stopPlaybackTimer()
        } catch (e: Exception) {
            // Только логируем ошибку, не меняем состояние UI
            Log.e(TAG, "Error pausing", e)
        }
    }

    private fun startPlaybackTimer() {
        // Отменяем предыдущую корутину, если она есть
        playbackJob?.cancel()
        // Запускаем новую корутину для обновления прогресса
        playbackJob = viewModelScope.launch {
            while (audioPlayerUseCase.isPlaying())  {
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
            Log.e(TAG, "Error releasing player", e)
        }
    }

    companion object {
        private const val TAG = "AudioPlayerViewModel"
        private const val PLAYBACK_UPDATE_DELAY = 300L
        private const val PREPARE_DELAY = 1000L
    }
}