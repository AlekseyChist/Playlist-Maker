package com.example.playlistmaker.player.ui.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
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

    fun preparePlayer(url: String) {
        _state.value = AudioPlayerState.Loading
        try {
            audioPlayerUseCase.preparePlayer(url)
            _state.value = AudioPlayerState.Prepared
        } catch (e: Exception) {
            _state.value = AudioPlayerState.Error(e.message ?: "Unknown error")
        }
    }

    fun play() {
        audioPlayerUseCase.play()
        _state.value = AudioPlayerState.Playing(audioPlayerUseCase.getCurrentPosition())
        startPlaybackTimer()
    }

    fun pause() {
        audioPlayerUseCase.pause()
        _state.value = AudioPlayerState.Paused
        stopPlaybackTimer()
    }

    private fun startPlaybackTimer() {
        playbackRunnable?.let { handler.removeCallbacks(it) }
        playbackRunnable = object : Runnable {
            override fun run() {
                if (audioPlayerUseCase.isPlaying()) {
                    _state.value = AudioPlayerState.Playing(audioPlayerUseCase.getCurrentPosition())
                    handler.postDelayed(this, PLAYBACK_UPDATE_DELAY)
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
        audioPlayerUseCase.release()
    }

    companion object {
        private const val PLAYBACK_UPDATE_DELAY = 300L
    }
}