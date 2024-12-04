package com.example.playlistmaker.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.usecases.settings.ThemeSettingsUseCase

class SettingsViewModel(
    private val themeSettingsUseCase: ThemeSettingsUseCase
) : ViewModel() {

    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    val darkThemeEnabled: LiveData<Boolean> = _darkThemeEnabled

    init {
        _darkThemeEnabled.value = themeSettingsUseCase.getDarkThemeEnabled()
    }

    fun switchTheme(enabled: Boolean) {
        themeSettingsUseCase.setDarkThemeEnabled(enabled)
        _darkThemeEnabled.value = enabled
    }
}