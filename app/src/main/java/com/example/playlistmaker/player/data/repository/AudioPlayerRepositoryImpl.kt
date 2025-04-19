package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.repository.AudioPlayerRepository

class AudioPlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : AudioPlayerRepository {

    private var isPrepared = false
    private var completionListener: (() -> Unit)? = null

    init {
        mediaPlayer.setOnPreparedListener {
            isPrepared = true
        }

        mediaPlayer.setOnErrorListener { _, _, _ ->
            isPrepared = false
            true
        }

        mediaPlayer.setOnCompletionListener {
            completionListener?.invoke()
        }
    }

    override fun preparePlayer(url: String) {
        try {
            isPrepared = false
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            isPrepared = false
            throw e
        }
    }

    override fun play() {
        if (!isPrepared) {
            return
        }

        try {
            mediaPlayer.start()
        } catch (e: IllegalStateException) {
            throw e
        }
    }

    override fun pause() {
        if (!isPrepared) {
            return
        }

        try {
            val isActuallyPlaying = try {
                mediaPlayer.isPlaying
            } catch (e: IllegalStateException) {
                false
            }

            if (isActuallyPlaying) {
                mediaPlayer.pause()
            }
        } catch (e: IllegalStateException) {
            // Логирование ошибки, если нужно
        }
    }

    override fun release() {
        try {
            mediaPlayer.release()
            isPrepared = false
        } catch (e: Exception) {
            // Логирование ошибки, если нужно
        }
    }

    override fun getCurrentPosition(): Int {
        return try {
            if (isPrepared) mediaPlayer.currentPosition else 0
        } catch (e: IllegalStateException) {
            0
        }
    }

    override fun isPlaying(): Boolean {
        return try {
            isPrepared && mediaPlayer.isPlaying
        } catch (e: IllegalStateException) {
            false
        }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        completionListener = listener
    }
}