package com.example.playlistmaker.settings.ui.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.settings.domain.theme.ThemeManager
import com.example.playlistmaker.settings.domain.usecase.ThemeSettingsUseCase
import com.example.playlistmaker.sharing.domain.usecase.SharingUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val themeSettingsUseCase: ThemeSettingsUseCase,
    private val sharingUseCase: SharingUseCase,
    private val themeManager: ThemeManager
) : ViewModel() {

    // 🆕 Используй StateFlow вместо LiveData
    val darkThemeEnabled: StateFlow<Boolean> = themeSettingsUseCase
        .observeDarkThemeEnabled()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = themeSettingsUseCase.getDarkThemeEnabled()
        )

    fun switchTheme(enabled: Boolean) {
        themeSettingsUseCase.setDarkThemeEnabled(enabled)
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