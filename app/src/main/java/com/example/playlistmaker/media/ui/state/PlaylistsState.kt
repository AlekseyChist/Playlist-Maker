package com.example.playlistmaker.media.ui.state

import com.example.playlistmaker.media.domain.model.Playlist

sealed class PlaylistsState {
    object Empty : PlaylistsState()
    data class Content(val playlists: List<Playlist>) : PlaylistsState()
}