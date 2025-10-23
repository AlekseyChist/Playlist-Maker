package com.example.playlistmaker.settings.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.settings.data.storage.SettingsStorage
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val settingsStorage: SettingsStorage
) : SettingsRepository {

    private val _darkThemeFlow = MutableStateFlow(settingsStorage.getDarkThemeEnabled())

    override fun getDarkThemeEnabled(): Boolean {
        return settingsStorage.getDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        settingsStorage.setDarkThemeEnabled(enabled)
        _darkThemeFlow.value = enabled // 🆕 Обнови Flow
    }

    override fun observeDarkThemeEnabled(): Flow<Boolean> {
        return _darkThemeFlow.asStateFlow()
    }
}