package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.data.network.RetrofitClient
import com.example.playlistmaker.search.data.network.iTunesApi
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.data.storage.SearchHistoryStorage
import com.example.playlistmaker.settings.data.storage.SettingsStorage
import com.example.playlistmaker.settings.data.storage.SettingsStorageImpl
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.settings.domain.usecase.ThemeSettingsUseCase
import com.example.playlistmaker.settings.domain.usecase.ThemeSettingsUseCaseImpl
import com.example.playlistmaker.search.domain.usecase.SearchHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SearchHistoryUseCaseImpl
import com.example.playlistmaker.search.domain.usecase.SearchTracksUseCase
import com.example.playlistmaker.search.domain.usecase.SearchTracksUseCaseImpl
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.data.theme.AppThemeManager
import com.example.playlistmaker.settings.domain.theme.ThemeManager
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.sharing.domain.usecase.SharingUseCase
import com.example.playlistmaker.sharing.domain.usecase.SharingUseCaseImpl


object Creator {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val sharedPreferences: SharedPreferences by lazy {
        requireNotNull(appContext).getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    // ViewModels
    fun provideSearchViewModel(): SearchViewModel {
        return SearchViewModel(
            provideSearchTracksUseCase(),
            provideSearchHistoryUseCase()
        )
    }

    private fun provideThemeManager(): ThemeManager {
        return AppThemeManager()
    }

    fun provideSettingsViewModel(): SettingsViewModel {
        return SettingsViewModel(
            provideThemeSettingsUseCase(),
            provideSharingUseCase(),
            provideThemeManager()
        )
    }

    fun provideAudioPlayerViewModel(): AudioPlayerViewModel {
        return AudioPlayerViewModel(provideMediaPlayer())
    }

    // Use Cases
    fun provideSearchTracksUseCase(): SearchTracksUseCase {
        return SearchTracksUseCaseImpl(provideTrackRepository())
    }

    fun provideSearchHistoryUseCase(): SearchHistoryUseCase {
        return SearchHistoryUseCaseImpl(provideTrackRepository())
    }

    fun provideThemeSettingsUseCase(): ThemeSettingsUseCase {
        return ThemeSettingsUseCaseImpl(provideSettingsRepository())
    }

    fun provideSharingUseCase(): SharingUseCase {
        return SharingUseCaseImpl(requireNotNull(appContext))
    }

    // Repositories
    private fun provideTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(
            provideiTunesApi(),
            provideSearchHistoryStorage(),
            provideTrackMapper()
        )
    }

    private fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(provideSettingsStorage())
    }

    // Storage
    private fun provideSearchHistoryStorage(): SearchHistoryStorage {
        return SearchHistoryStorage(sharedPreferences)
    }

    private fun provideSettingsStorage(): SettingsStorage {
        return SettingsStorageImpl(sharedPreferences)
    }

    // API
    private fun provideiTunesApi(): iTunesApi {
        return RetrofitClient.iTunesApi
    }

    // Mapper
    private fun provideTrackMapper(): TrackMapper {
        return TrackMapper()
    }

    // MediaPlayer
    fun provideMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }
}