package com.example.playlistmaker.di

import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.data.network.RetrofitClient
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.data.storage.SearchHistoryStorage
import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.search.domain.usecase.SearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SearchHistoryUseCaseImpl
import com.example.playlistmaker.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.search.domain.usecase.SearchTracksUseCaseImpl
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    // Data Layer
    single { RetrofitClient.iTunesApi }
    single { TrackMapper() }
    single { SearchHistoryStorage(get()) }
    single<TrackRepository> {
        TrackRepositoryImpl(
            api = get(),
            searchHistoryStorage = get(),
            mapper = get()
        )
    }

    // Domain Layer
    single<SearchTracksUseCase> { SearchTracksUseCaseImpl(get()) }
    single<SearchHistoryUseCase> { SearchHistoryUseCaseImpl(get()) }

    // UI Layer
    viewModel { SearchViewModel(get(), get()) }
}