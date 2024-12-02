package com.example.playlistmaker.di

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        applyTheme()
    }

    private fun applyTheme() {
        val themeSettingsUseCase = Creator.provideThemeSettingsUseCase()
        val darkThemeEnabled = themeSettingsUseCase.getDarkThemeEnabled()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}


