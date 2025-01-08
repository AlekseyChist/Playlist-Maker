package com.example.playlistmaker.di

import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.storage.SettingsStorage
import com.example.playlistmaker.settings.data.storage.SettingsStorageImpl
import com.example.playlistmaker.settings.data.theme.AppThemeManager
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.settings.domain.theme.ThemeManager
import com.example.playlistmaker.settings.domain.usecase.ThemeSettingsUseCase
import com.example.playlistmaker.settings.domain.usecase.ThemeSettingsUseCaseImpl
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    // Data Layer
    single<SettingsStorage> { SettingsStorageImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<ThemeManager> { AppThemeManager() }

    // Domain Layer
    single<ThemeSettingsUseCase> { ThemeSettingsUseCaseImpl(get()) }

    // UI Layer
    viewModel {
        SettingsViewModel(
            themeSettingsUseCase = get(),
            sharingUseCase = get(),
            themeManager = get()
        )
    }
}