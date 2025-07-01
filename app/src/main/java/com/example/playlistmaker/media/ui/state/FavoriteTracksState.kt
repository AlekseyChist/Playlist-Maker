package com.example.playlistmaker.media.ui.state

import com.example.playlistmaker.search.domain.model.Track

sealed class FavoriteTracksState {
    object Empty : FavoriteTracksState()
    data class Content(val tracks: List<Track>) : FavoriteTracksState()
}