package com.example.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
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
}