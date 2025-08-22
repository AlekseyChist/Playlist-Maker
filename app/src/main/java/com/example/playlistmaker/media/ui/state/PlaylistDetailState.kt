package com.example.playlistmaker.media.ui.state

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track

sealed class PlaylistDetailState {
    object Loading : PlaylistDetailState()
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val totalDuration: String
    ) : PlaylistDetailState()
    object Error : PlaylistDetailState()
}