package com.example.playlistmaker.settings.data.repository

import com.example.playlistmaker.settings.data.storage.SettingsStorage
import com.example.playlistmaker.settings.domain.repository.SettingsRepository


class SettingsRepositoryImpl(
    private val settingsStorage: SettingsStorage
) : SettingsRepository {

    override fun getDarkThemeEnabled(): Boolean {
        return settingsStorage.getDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        settingsStorage.setDarkThemeEnabled(enabled)
    }
}