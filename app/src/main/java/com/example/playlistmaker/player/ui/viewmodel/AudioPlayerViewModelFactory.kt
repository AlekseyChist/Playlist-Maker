package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.usecase.AudioPlayerUseCase

class AudioPlayerViewModelFactory(
    private val audioPlayerUseCase: AudioPlayerUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AudioPlayerViewModel(audioPlayerUseCase) as T
    }
}