package com.example.playlistmaker.settings.data.storage

import android.content.SharedPreferences

interface SettingsStorage {
    fun getDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}

/**
 * Реализация хранения настроек с использованием SharedPreferences
 */
class SettingsStorageImpl(private val sharedPreferences: SharedPreferences) : SettingsStorage {

    companion object {
        private const val KEY_DARK_THEME = "dark_theme"
    }

    override fun getDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
    }
}