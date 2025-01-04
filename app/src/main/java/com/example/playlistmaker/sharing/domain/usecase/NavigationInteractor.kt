package com.example.playlistmaker.sharing.domain.usecase

interface NavigationInteractor {
    fun shareText(text: String)
    fun openLink(url: String)
    fun sendEmail(email: String, subject: String, body: String)
}