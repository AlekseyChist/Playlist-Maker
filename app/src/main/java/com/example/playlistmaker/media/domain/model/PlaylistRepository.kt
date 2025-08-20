package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(playlistId: Long): Playlist?
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
}