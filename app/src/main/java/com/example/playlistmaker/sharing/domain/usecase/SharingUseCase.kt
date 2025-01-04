package com.example.playlistmaker.sharing.domain.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R


interface SharingUseCase {
    fun shareApp()
    fun writeToSupport(email: String, subject: String, body: String)
    fun openUserAgreement(url: String)
}

class SharingUseCaseImpl(
    private val navigationInteractor: NavigationInteractor
) : SharingUseCase {
    override fun shareApp() {
        navigationInteractor.shareText("App share text")
    }

    override fun writeToSupport(email: String, subject: String, body: String) {
        navigationInteractor.sendEmail(email, subject, body)
    }

    override fun openUserAgreement(url: String) {
        navigationInteractor.openLink(url)
    }
}