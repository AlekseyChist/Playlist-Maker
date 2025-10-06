package com.example.playlistmaker.di

import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    // UI Layer
    viewModel {
        AudioPlayerViewModel(
            favoriteTracksInteractor = get(),
            playlistInteractor = get()
        )
    }
}