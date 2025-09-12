package com.example.playlistmaker.media.domain.usecase

import android.net.Uri
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String?, coverUri: Uri?): Long
    suspend fun updatePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(playlistId: Long): Playlist?
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)
    suspend fun getPlaylistTracks(trackIds: List<Long>): List<Track>
    suspend fun deletePlaylist(playlistId: Long) // Новый метод
    fun generateShareText(playlist: Playlist, tracks: List<Track>): String // Новый метод
    suspend fun saveCover(uri: Uri): String
}

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
    private val coverStorage: CoverStorage
) : PlaylistInteractor {

    override suspend fun saveCover(uri: Uri): String {
        return coverStorage.saveCover(uri)
    }

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

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getPlaylistTracks(trackIds: List<Long>): List<Track> {
        return playlistRepository.getPlaylistTracks(trackIds)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistRepository.deletePlaylistById(playlistId)
    }


    override fun generateShareText(playlist: Playlist, tracks: List<Track>): String {
        val stringBuilder = StringBuilder()

        // Название плейлиста
        stringBuilder.append(playlist.name)
        stringBuilder.append("\n")

        // Описание (если есть)
        if (!playlist.description.isNullOrEmpty()) {
            stringBuilder.append(playlist.description)
            stringBuilder.append("\n")
        }

        // Количество треков
        val tracksCountText = when {
            tracks.size % 10 == 1 && tracks.size % 100 != 11 -> "${tracks.size} трек"
            tracks.size % 10 in 2..4 && tracks.size % 100 !in 12..14 -> "${tracks.size} трека"
            else -> "${tracks.size} треков"
        }
        stringBuilder.append(tracksCountText)
        stringBuilder.append("\n")

        // Список треков
        tracks.forEachIndexed { index, track ->
            val minutes = track.trackTimeMillis / 60000
            val seconds = (track.trackTimeMillis % 60000) / 1000
            val duration = String.format("%d:%02d", minutes, seconds)

            stringBuilder.append("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
            if (index < tracks.size - 1) {
                stringBuilder.append("\n")
            }
        }

        return stringBuilder.toString()
    }
}