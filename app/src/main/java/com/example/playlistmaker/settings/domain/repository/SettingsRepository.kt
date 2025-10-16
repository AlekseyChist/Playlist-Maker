package com.example.playlistmaker.settings.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
    fun observeDarkThemeEnabled(): Flow<Boolean>
}