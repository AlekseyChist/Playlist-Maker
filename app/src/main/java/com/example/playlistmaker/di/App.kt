package com.example.playlistmaker.di

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.usecases.settings.ThemeSettingsUseCase
import com.example.playlistmaker.domain.usecases.tracks.SearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.tracks.SearchTracksUseCase

class App : Application() {

    private lateinit var creator: Creator

    // Use Cases
    private lateinit var searchTracksUseCase: SearchTracksUseCase
    private lateinit var searchHistoryUseCase: SearchHistoryUseCase
    private lateinit var themeSettingsUseCase: ThemeSettingsUseCase

    override fun onCreate() {
        super.onCreate()

        creator = Creator(this)

        // Инициализация Use Cases
        searchTracksUseCase = creator.searchTracksUseCase
        searchHistoryUseCase = creator.searchHistoryUseCase
        themeSettingsUseCase = creator.themeSettingsUseCase

        // Применяем сохраненную тему
        applyTheme()
    }

    private fun applyTheme() {
        val darkThemeEnabled = themeSettingsUseCase.getDarkThemeEnabled()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    // Геттеры для Use Cases
    fun getSearchTracksUseCase(): SearchTracksUseCase = searchTracksUseCase
    fun getSearchHistoryUseCase(): SearchHistoryUseCase = searchHistoryUseCase
    fun getThemeSettingsUseCase(): ThemeSettingsUseCase = themeSettingsUseCase

    // Для MediaPlayer создаем новый экземпляр каждый раз
    fun getMediaPlayer(): MediaPlayer = creator.provideMediaPlayer()
}
