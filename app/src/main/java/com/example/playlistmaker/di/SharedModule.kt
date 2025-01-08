package com.example.playlistmaker.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedModule = module {
    single {
        androidContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }
}