package com.example.playlistmaker.player.domain.usecase

import com.example.playlistmaker.player.domain.repository.AudioPlayerRepository

interface AudioPlayerUseCase {
    fun preparePlayer(url: String)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}

class AudioPlayerUseCaseImpl(
    private val repository: AudioPlayerRepository
) : AudioPlayerUseCase {
    override fun preparePlayer(url: String) = repository.preparePlayer(url)
    override fun play() = repository.play()
    override fun pause() = repository.pause()
    override fun release() = repository.release()
    override fun getCurrentPosition() = repository.getCurrentPosition()
    override fun isPlaying() = repository.isPlaying()
}