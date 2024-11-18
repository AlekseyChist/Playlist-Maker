package com.example.playlistmaker.presentation.state


sealed class AudioPlayerState {
    object Loading : AudioPlayerState()
    object Prepared : AudioPlayerState()
    data class Playing(val currentPosition: Int) : AudioPlayerState()
    object Paused : AudioPlayerState()
    data class Error(val message: String) : AudioPlayerState()
}