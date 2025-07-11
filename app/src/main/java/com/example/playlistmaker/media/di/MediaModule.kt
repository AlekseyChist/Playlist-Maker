package com.example.playlistmaker.media.di

import androidx.room.Room
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.converter.TrackDbConverter
import com.example.playlistmaker.media.data.repository.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.media.domain.usecase.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.usecase.FavoriteTracksInteractorImpl
import com.example.playlistmaker.media.ui.viewmodel.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.viewmodel.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module

val mediaModule = module {


    // Converter
    single { TrackDbConverter() }

    // Repository
    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(
            database = get(),
            trackDbConverter = get()
        )
    }

    // Interactor
    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(repository = get())
    }

    // ViewModels
    viewModel {
        FavoriteTracksViewModel(interactor = get())
    }
    viewModel {
        PlaylistsViewModel()
    }
}