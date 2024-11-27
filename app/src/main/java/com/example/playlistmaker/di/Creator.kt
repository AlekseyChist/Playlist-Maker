package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.data.mappers.TrackMapper
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.network.iTunesApi
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

object Creator {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val sharedPreferences: SharedPreferences by lazy {
        requireNotNull(appContext).getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    // Use Case providers
    fun provideSearchTracksUseCase(): SearchTracksUseCase {
        return SearchTracksUseCaseImpl(provideTrackRepository())
    }

    fun provideSearchHistoryUseCase(): SearchHistoryUseCase {
        return SearchHistoryUseCaseImpl(provideTrackRepository())
    }

    fun provideThemeSettingsUseCase(): ThemeSettingsUseCase {
        return ThemeSettingsUseCaseImpl(provideSettingsRepository())
    }

    // Repository providers
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

    // Storage providers
    private fun provideSearchHistoryStorage(): SearchHistoryStorage {
        return SearchHistoryStorage(sharedPreferences)
    }

    private fun provideSettingsStorage(): SettingsStorage {
        return SettingsStorageImpl(sharedPreferences)
    }

    // API provider
    private fun provideiTunesApi(): iTunesApi {
        return RetrofitClient.iTunesApi
    }

    // Mapper provider
    private fun provideTrackMapper(): TrackMapper {
        return TrackMapper()
    }

    // MediaPlayer provider
    fun provideMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }
}