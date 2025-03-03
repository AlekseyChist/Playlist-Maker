package com.example.playlistmaker.player.ui.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.usecase.AudioPlayerUseCase
import com.example.playlistmaker.player.ui.state.AudioPlayerState

class AudioPlayerViewModel(
    private val audioPlayerUseCase: AudioPlayerUseCase
) : ViewModel() {

    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    private val handler = Handler(Looper.getMainLooper())
    private var playbackRunnable: Runnable? = null

    init {
        _state.value = AudioPlayerState.Loading
    }

    fun preparePlayer(url: String) {
        _state.value = AudioPlayerState.Loading
        try {
            // Подготавливаем плеер
            audioPlayerUseCase.preparePlayer(url)

            // Поскольку у нас нет возможности получить колбэк о готовности,
            // используем разумную задержку.
            // В реальной ситуации лучше было бы добавить в интерфейс механизм колбэков.
            handler.postDelayed({
                if (_state.value is AudioPlayerState.Loading) {
                    _state.value = AudioPlayerState.Prepared
                }
            }, PREPARE_DELAY)
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
        playbackRunnable?.let { handler.removeCallbacks(it) }

        playbackRunnable = object : Runnable {
            override fun run() {
                try {
                    if (audioPlayerUseCase.isPlaying()) {
                        _state.value = AudioPlayerState.Playing(audioPlayerUseCase.getCurrentPosition())
                        handler.postDelayed(this, PLAYBACK_UPDATE_DELAY)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in playback timer", e)
                    stopPlaybackTimer()
                }
            }
        }

        handler.post(playbackRunnable!!)
    }

    private fun stopPlaybackTimer() {
        playbackRunnable?.let { handler.removeCallbacks(it) }
        playbackRunnable = null
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