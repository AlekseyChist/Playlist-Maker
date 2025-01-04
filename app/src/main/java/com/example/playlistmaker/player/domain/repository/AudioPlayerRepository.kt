package com.example.playlistmaker.player.domain.repository

interface AudioPlayerRepository {
    fun preparePlayer(url: String)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}