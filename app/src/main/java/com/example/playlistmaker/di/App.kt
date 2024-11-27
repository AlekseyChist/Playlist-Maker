package com.example.playlistmaker.di

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
    }
}


