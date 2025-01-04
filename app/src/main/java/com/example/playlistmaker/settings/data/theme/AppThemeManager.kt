package com.example.playlistmaker.settings.data.theme

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.theme.ThemeManager

class AppThemeManager : ThemeManager {
    override fun setNightMode(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}