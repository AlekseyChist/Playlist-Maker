package com.example.playlistmaker.player.ui.state


sealed class AudioPlayerState {
    object Loading : AudioPlayerState()
    object Prepared : AudioPlayerState()
    data class Playing(val currentPosition: Int) : AudioPlayerState()
    object Paused : AudioPlayerState()
    data class Error(val message: String) : AudioPlayerState()
}