package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.player.domain.usecase.AudioPlayerUseCase
import com.example.playlistmaker.player.domain.usecase.AudioPlayerUseCaseImpl
import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    // Data Layer
    factory { MediaPlayer() }
    factory<AudioPlayerRepository> { AudioPlayerRepositoryImpl(get()) }

    // Domain Layer
    factory<AudioPlayerUseCase> { AudioPlayerUseCaseImpl(get()) }

    // UI Layer
    viewModel { AudioPlayerViewModel(get()) }
}