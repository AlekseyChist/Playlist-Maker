package com.example.playlistmaker.media.data.repository

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.converter.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.converter.TrackDbConverter
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val database: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter
) : PlaylistRepository {

    private val gson = Gson()

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

    override suspend fun deletePlaylistById(playlistId: Long) {
        // Получаем плейлист перед удалением для очистки треков
        val playlist = getPlaylistById(playlistId)

        // Удаляем сам плейлист
        database.playlistDao().deletePlaylistById(playlistId)

        // Очищаем неиспользуемые треки
        playlist?.trackIds?.forEach { trackId ->
            cleanupUnusedTrack(trackId)
        }
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
        val trackEntity = trackDbConverter.mapTrackToPlaylistEntity(track)
        database.playlistTracksDao().insertTrack(trackEntity)

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

        cleanupUnusedTrack(trackId)
    }

    private suspend fun cleanupUnusedTrack(trackId: Long) {
        val playlistCount = database.playlistTracksDao().countPlaylistsContainingTrack(trackId)
        if (playlistCount == 0) {
            database.playlistTracksDao().deleteTrack(trackId)
        }
    }

    override suspend fun getPlaylistTracks(trackIds: List<Long>): List<Track> {
        if (trackIds.isEmpty()) return emptyList()

        val trackEntities = database.playlistTracksDao().getTracksByIds(trackIds)
        return trackEntities.map { entity ->
            trackDbConverter.mapPlaylistEntityToTrack(entity)
        }
    }
}