package com.example.playlistmaker.di

import com.example.playlistmaker.sharing.data.AndroidNavigationInteractor
import com.example.playlistmaker.sharing.domain.usecase.NavigationInteractor
import com.example.playlistmaker.sharing.domain.usecase.SharingUseCase
import com.example.playlistmaker.sharing.domain.usecase.SharingUseCaseImpl
import org.koin.dsl.module

val sharingModule = module {
    single<NavigationInteractor> { AndroidNavigationInteractor(get()) }
    single<SharingUseCase> { SharingUseCaseImpl(get()) }
}