package com.example.playlistmaker.media.data.repository

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.converter.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.converter.TrackDbConverter
import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val database: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        val entity = playlistDbConverter.mapPlaylistToEntity(playlist)
        return database.playlistDao().insertPlaylist(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = playlistDbConverter.mapPlaylistToEntity(playlist)
        database.playlistDao().updatePlaylist(entity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val entity = playlistDbConverter.mapPlaylistToEntity(playlist)
        database.playlistDao().deletePlaylist(entity)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return database.playlistDao().getAllPlaylists()
            .map { entities ->
                entities.map { entity ->
                    playlistDbConverter.mapEntityToPlaylist(entity)
                }
            }
    }

    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return database.playlistDao().getPlaylistById(playlistId)?.let { entity ->
            playlistDbConverter.mapEntityToPlaylist(entity)
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        // Добавляем трек в таблицу playlist_tracks
        val trackEntity = PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
        database.playlistTracksDao().insertTrack(trackEntity)

        // Обновляем плейлист
        val updatedTrackIds = playlist.trackIds + track.trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )
        updatePlaylist(updatedPlaylist)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        val playlist = getPlaylistById(playlistId) ?: return
        val updatedTrackIds = playlist.trackIds - trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )
        updatePlaylist(updatedPlaylist)
    }
}