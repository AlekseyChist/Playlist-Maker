package com.example.playlistmaker.settings.ui.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.theme.ThemeManager
import com.example.playlistmaker.settings.domain.usecase.ThemeSettingsUseCase
import com.example.playlistmaker.sharing.domain.usecase.SharingUseCase

class SettingsViewModel(
    private val themeSettingsUseCase: ThemeSettingsUseCase,
    private val sharingUseCase: SharingUseCase,
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    val darkThemeEnabled: LiveData<Boolean> = _darkThemeEnabled

    init {
        _darkThemeEnabled.value = themeSettingsUseCase.getDarkThemeEnabled()
    }

    fun switchTheme(enabled: Boolean) {
        themeSettingsUseCase.setDarkThemeEnabled(enabled)
        _darkThemeEnabled.value = enabled
        themeManager.setNightMode(enabled)
    }

    fun shareApp() {
        sharingUseCase.shareApp()
    }

    fun writeToSupport(email: String, subject: String, body: String) {
        sharingUseCase.writeToSupport(email, subject, body)
    }

    fun openUserAgreement(url: String) {
        sharingUseCase.openUserAgreement(url)
    }
}