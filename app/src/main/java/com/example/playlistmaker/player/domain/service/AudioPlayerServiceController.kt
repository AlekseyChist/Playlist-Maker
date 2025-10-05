package com.example.playlistmaker.player.domain.service

interface AudioPlayerServiceController {
    fun preparePlayer(url: String)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun setOnCompletionListener(listener: () -> Unit)
    fun showNotification()
    fun hideNotification()
}

data class PlayerState(
    val isPlaying: Boolean,
    val currentPosition: Int,
    val isPrepared: Boolean
)