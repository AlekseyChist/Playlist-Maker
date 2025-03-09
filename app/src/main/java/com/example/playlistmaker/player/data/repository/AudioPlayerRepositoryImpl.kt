package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import android.util.Log
import com.example.playlistmaker.player.domain.repository.AudioPlayerRepository

class AudioPlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : AudioPlayerRepository {

    private var isPrepared = false

    init {
        mediaPlayer.setOnPreparedListener {
            isPrepared = true
            Log.d(TAG, "MediaPlayer prepared")
        }

        mediaPlayer.setOnErrorListener { _, what, extra ->
            isPrepared = false
            Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
            true
        }

        mediaPlayer.setOnCompletionListener {
            Log.d(TAG, "MediaPlayer playback completed")
        }
    }

    override fun preparePlayer(url: String) {
        try {
            isPrepared = false
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            Log.d(TAG, "MediaPlayer preparing: $url")
        } catch (e: Exception) {
            isPrepared = false
            Log.e(TAG, "Error preparing MediaPlayer", e)
            throw e
        }
    }

    override fun play() {
        if (!isPrepared) {
            Log.d(TAG, "Cannot play: MediaPlayer not prepared")
            return
        }

        try {
            mediaPlayer.start()
            Log.d(TAG, "MediaPlayer started")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error starting MediaPlayer", e)
            throw e
        }
    }

    override fun pause() {
        if (!isPrepared) {
            Log.d(TAG, "Cannot pause: MediaPlayer not prepared")
            return
        }

        try {
            val isActuallyPlaying = try {
                mediaPlayer.isPlaying
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Error checking isPlaying", e)
                false
            }

            if (isActuallyPlaying) {
                mediaPlayer.pause()
                Log.d(TAG, "MediaPlayer paused")
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error pausing MediaPlayer", e)
        }
    }

    override fun release() {
        try {
            mediaPlayer.release()
            isPrepared = false
            Log.d(TAG, "MediaPlayer released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MediaPlayer", e)
        }
    }

    override fun getCurrentPosition(): Int {
        return try {
            if (isPrepared) mediaPlayer.currentPosition else 0
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error getting current position", e)
            0
        }
    }

    override fun isPlaying(): Boolean {
        return try {
            isPrepared && mediaPlayer.isPlaying
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error checking isPlaying", e)
            false
        }
    }

    companion object {
        private const val TAG = "AudioPlayerRepository"
    }
}