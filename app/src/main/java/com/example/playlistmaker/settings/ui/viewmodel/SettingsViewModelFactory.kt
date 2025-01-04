package com.example.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.settings.domain.theme.ThemeManager
import com.example.playlistmaker.settings.domain.usecase.ThemeSettingsUseCase
import com.example.playlistmaker.sharing.domain.usecase.SharingUseCase

class SettingsViewModelFactory(
    private val themeSettingsUseCase: ThemeSettingsUseCase,
    private val sharingUseCase: SharingUseCase,
    private val themeManager: ThemeManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(themeSettingsUseCase, sharingUseCase, themeManager) as T
    }
}