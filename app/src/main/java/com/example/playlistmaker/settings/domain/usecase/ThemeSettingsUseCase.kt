package com.example.playlistmaker.settings.domain.usecase

import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

interface ThemeSettingsUseCase {
    fun getDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
    fun observeDarkThemeEnabled(): Flow<Boolean> // 🆕
}

class ThemeSettingsUseCaseImpl(
    private val repository: SettingsRepository
) : ThemeSettingsUseCase {
    override fun getDarkThemeEnabled(): Boolean {
        return repository.getDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        repository.setDarkThemeEnabled(enabled)
    }

    override fun observeDarkThemeEnabled(): Flow<Boolean> {
        return repository.observeDarkThemeEnabled()
    }
}