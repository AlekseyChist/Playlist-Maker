package com.example.playlistmaker.media.di

import androidx.room.Room
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.converter.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.converter.TrackDbConverter
import com.example.playlistmaker.media.data.repository.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.media.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.media.data.storage.CoverStorageImpl
import com.example.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.media.domain.usecase.CoverStorage
import com.example.playlistmaker.media.domain.usecase.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.usecase.FavoriteTracksInteractorImpl
import com.example.playlistmaker.media.domain.usecase.PlaylistInteractor
import com.example.playlistmaker.media.domain.usecase.PlaylistInteractorImpl
import com.example.playlistmaker.media.ui.viewmodel.CreatePlaylistViewModel
import com.example.playlistmaker.media.ui.viewmodel.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.viewmodel.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module

val mediaModule = module {

    // Converters
    single { TrackDbConverter() }
    single { PlaylistDbConverter() }

    // Repository
    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(
            database = get(),
            trackDbConverter = get()
        )
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            database = get(),
            playlistDbConverter = get()
        )
    }

    // Storage
    single<CoverStorage> { CoverStorageImpl(get()) }

    // Interactor
    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(repository = get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(
            playlistRepository = get(),
            coverStorage = get()
        )
    }

    // ViewModels
    viewModel { FavoriteTracksViewModel(interactor = get()) }
    viewModel { PlaylistsViewModel(playlistInteractor = get()) } // Передаем зависимость
    viewModel { CreatePlaylistViewModel(playlistInteractor = get()) }
}