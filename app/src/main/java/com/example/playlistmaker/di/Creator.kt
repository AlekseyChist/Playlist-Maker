package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.data.mappers.TrackMapper
import com.example.playlistmaker.data.network.iTunesApiService
import com.example.playlistmaker.data.repositories.SettingsRepositoryImpl
import com.example.playlistmaker.data.repositories.TrackRepositoryImpl
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.data.storage.SettingsStorage
import com.example.playlistmaker.data.storage.SettingsStorageImpl
import com.example.playlistmaker.domain.repositories.SettingsRepository
import com.example.playlistmaker.domain.repositories.TrackRepository
import com.example.playlistmaker.domain.usecases.settings.ThemeSettingsUseCase
import com.example.playlistmaker.domain.usecases.settings.ThemeSettingsUseCaseImpl
import com.example.playlistmaker.domain.usecases.tracks.SearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.tracks.SearchHistoryUseCaseImpl
import com.example.playlistmaker.domain.usecases.tracks.SearchTracksUseCase
import com.example.playlistmaker.domain.usecases.tracks.SearchTracksUseCaseImpl

class Creator(private val context: Context) {
    // Storage
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    private val searchHistoryStorage: SearchHistoryStorage by lazy {
        SearchHistoryStorage(sharedPreferences)
    }

    private val settingsStorage: SettingsStorage by lazy {
        SettingsStorageImpl(sharedPreferences)
    }

    // Mappers
    private val trackMapper: TrackMapper by lazy {
        TrackMapper()
    }

    // Repositories
    private val trackRepository: TrackRepository by lazy {
        TrackRepositoryImpl(iTunesApiService, searchHistoryStorage, trackMapper)
    }

    private val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(settingsStorage)
    }

    // Use Cases
    val searchTracksUseCase: SearchTracksUseCase by lazy {
        SearchTracksUseCaseImpl(trackRepository)
    }

    val searchHistoryUseCase: SearchHistoryUseCase by lazy {
        SearchHistoryUseCaseImpl(trackRepository)
    }

    val themeSettingsUseCase: ThemeSettingsUseCase by lazy {
        ThemeSettingsUseCaseImpl(settingsRepository)
    }

    // MediaPlayer
    fun provideMediaPlayer(): MediaPlayer = MediaPlayer()
}