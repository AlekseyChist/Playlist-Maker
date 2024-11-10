package com.example.playlistmaker.data.repositories

import com.example.playlistmaker.data.storage.SettingsStorage
import com.example.playlistmaker.domain.repositories.SettingsRepository


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