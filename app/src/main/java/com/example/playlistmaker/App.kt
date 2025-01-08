package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

import com.example.playlistmaker.di.playerModule
import com.example.playlistmaker.di.searchModule
import com.example.playlistmaker.di.settingsModule
import com.example.playlistmaker.di.sharedModule
import com.example.playlistmaker.di.sharingModule
import com.example.playlistmaker.settings.domain.usecase.ThemeSettingsUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin


class App : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                sharedModule,
                searchModule,
                playerModule,
                settingsModule,
                sharingModule
            )
        }

        applyTheme()
    }

    private fun applyTheme() {
        val themeSettingsUseCase: ThemeSettingsUseCase by inject()
        val darkThemeEnabled = themeSettingsUseCase.getDarkThemeEnabled()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}


