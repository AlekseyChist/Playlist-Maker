package com.example.playlistmaker.di

import com.example.playlistmaker.sharing.data.AndroidNavigationInteractor
import com.example.playlistmaker.sharing.domain.usecase.NavigationInteractor
import com.example.playlistmaker.sharing.domain.usecase.SharingInteractor
import com.example.playlistmaker.sharing.domain.usecase.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.usecase.SharingUseCase
import com.example.playlistmaker.sharing.domain.usecase.SharingUseCaseImpl
import org.koin.dsl.module

val sharingModule = module {
    single<NavigationInteractor> { AndroidNavigationInteractor(get()) }
    single<SharingUseCase> { SharingUseCaseImpl(get()) }
    single<SharingInteractor> { SharingInteractorImpl(get()) }
}