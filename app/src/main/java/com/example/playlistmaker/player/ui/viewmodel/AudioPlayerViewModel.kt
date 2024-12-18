package com.example.playlistmaker.player.ui.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.ui.state.AudioPlayerState

class AudioPlayerViewModel(
    private val mediaPlayer: MediaPlayer
) : ViewModel() {

    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    private val handler = Handler(Looper.getMainLooper())
    private var playbackRunnable: Runnable? = null


    fun preparePlayer(url: String) {
        _state.value = AudioPlayerState.Loading
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                _state.value = AudioPlayerState.Prepared
            }
            mediaPlayer.setOnCompletionListener {
                stopPlayback()
            }
        } catch (e: Exception) {
            _state.value = AudioPlayerState.Error(e.message ?: "Unknown error")
        }
    }

    fun play() {
        mediaPlayer.start()
        _state.value = AudioPlayerState.Playing(mediaPlayer.currentPosition)
        startPlaybackTimer()
    }

    fun pause() {
        mediaPlayer.pause()
        _state.value = AudioPlayerState.Paused
        stopPlaybackTimer()
    }

    private fun startPlaybackTimer() {
        playbackRunnable?.let { handler.removeCallbacks(it) }
        playbackRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    _state.value = AudioPlayerState.Playing(mediaPlayer.currentPosition)
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

    private fun stopPlayback() {
        mediaPlayer.pause()
        mediaPlayer.seekTo(0)
        _state.value = AudioPlayerState.Prepared
        stopPlaybackTimer()
    }

    override fun onCleared() {
        super.onCleared()
        stopPlaybackTimer()
        mediaPlayer.release()
    }

    companion object {
        private const val PLAYBACK_UPDATE_DELAY = 300L
    }
}