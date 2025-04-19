package com.example.playlistmaker.search.data.repository

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
    private val mapper: TrackMapper
) : TrackRepository {

    override fun searchTracks(query: String): Flow<List<Track>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }

        try {
            val response = api.search(query, "song")
            val tracks = response.results.map { mapper.mapDtoToDomain(it) }
            emit(tracks)
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

    override fun getSearchHistory(): List<Track> {
        return searchHistoryStorage.getTracks().map { mapper.mapDtoToDomain(it) }
    }

    override fun clearSearchHistory() {
        searchHistoryStorage.clearHistory()
    }
}