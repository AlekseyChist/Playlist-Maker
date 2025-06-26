package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.data.network.iTunesApi
import com.example.playlistmaker.search.data.storage.SearchHistoryStorage
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.cancellation.CancellationException

class TrackRepositoryImpl(
    private val api: iTunesApi,
    private val searchHistoryStorage: SearchHistoryStorage,
    private val mapper: TrackMapper,
    private val database: AppDatabase
) : TrackRepository {

    override fun searchTracks(query: String): Flow<List<Track>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }

        try {
            val response = api.search(query, "song")
            val tracks = response.results.map { mapper.mapDtoToDomain(it) }

            // Проверяем какие треки находятся в избранном
            val favoriteIds = database.favoriteTracksDao().getFavoriteTrackIds()
            val tracksWithFavoriteStatus = tracks.map { track ->
                track.copy(isFavorite = favoriteIds.contains(track.trackId))
            }

            emit(tracksWithFavoriteStatus)
        } catch (e: CancellationException) {
            // Не перехватываем CancellationException, а передаем его выше
            throw e
        } catch (e: Exception) {
            // Остальные исключения перехватываем и обрабатываем
            throw e
        }
    }

    override fun addTrackToHistory(track: Track) {
        searchHistoryStorage.addTrack(mapper.mapDomainToDto(track))
    }

    override suspend fun getSearchHistory(): List<Track> {
        val historyTracks = searchHistoryStorage.getTracks().map { mapper.mapDtoToDomain(it) }

        // Проверяем статус избранного для треков из истории
        val favoriteIds = database.favoriteTracksDao().getFavoriteTrackIds()
        return historyTracks.map { track ->
            track.copy(isFavorite = favoriteIds.contains(track.trackId))
        }
    }

    override fun clearSearchHistory() {
        searchHistoryStorage.clearHistory()
    }
}