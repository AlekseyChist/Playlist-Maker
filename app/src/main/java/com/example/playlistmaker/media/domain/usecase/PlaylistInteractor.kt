package com.example.playlistmaker.media.domain.usecase

import android.net.Uri
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String?, coverUri: Uri?): Long
    suspend fun updatePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(playlistId: Long): Playlist?
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)
}

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
    private val coverStorage: CoverStorage
) : PlaylistInteractor {

    override suspend fun createPlaylist(name: String, description: String?, coverUri: Uri?): Long {
        val coverPath = coverUri?.let { uri ->
            coverStorage.saveCover(uri)
        }

        val playlist = Playlist(
            name = name,
            description = description,
            coverPath = coverPath
        )

        return playlistRepository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        playlistRepository.addTrackToPlaylist(playlistId, trackId)
    }
}