package com.example.playlistmaker.sharing.domain.usecase

interface SharingInteractor {
    fun sharePlaylist(text: String)
}

class SharingInteractorImpl(
    private val navigationInteractor: NavigationInteractor
) : SharingInteractor {

    override fun sharePlaylist(text: String) {
        navigationInteractor.shareText(text)
    }
}