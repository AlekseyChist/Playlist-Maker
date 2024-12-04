package com.example.playlistmaker.domain.repositories

interface SettingsRepository {
    fun getDarkThemeEnabled(): Boolean

    fun setDarkThemeEnabled(enabled: Boolean)
}