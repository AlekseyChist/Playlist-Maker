package com.example.playlistmaker.media.data.repository

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.converter.TrackDbConverter
import com.example.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val database: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        val entity = trackDbConverter.mapTrackToEntity(track)
        database.favoriteTracksDao().insertTrack(entity)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        val entity = trackDbConverter.mapTrackToEntity(track)
        database.favoriteTracksDao().deleteTrack(entity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return database.favoriteTracksDao().getAllFavoriteTracksFlow()
            .map { entities ->
                entities.map { entity ->
                    trackDbConverter.mapEntityToTrack(entity)
                }
            }
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return database.favoriteTracksDao().isTrackFavorite(trackId)
    }
}