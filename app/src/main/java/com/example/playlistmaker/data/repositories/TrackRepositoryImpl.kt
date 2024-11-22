package com.example.playlistmaker.data.repositories

import com.example.playlistmaker.data.mappers.TrackMapper
import com.example.playlistmaker.data.network.iTunesApi
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repositories.TrackRepository

class TrackRepositoryImpl(
    private val api: iTunesApi,
    private val searchHistoryStorage: SearchHistoryStorage,
    private val mapper: TrackMapper
) : TrackRepository {

    override fun searchTracks(query: String): List<Track> {
        return runCatching {
            val call = api.search(query)
            val response = call.execute()
            if (response.isSuccessful) {
                response.body()?.results?.map { mapper.mapDtoToDomain(it) } ?: emptyList()
            } else {
                emptyList()
            }
        }.getOrElse {
            emptyList()
        }
    }

    override fun addTrackToHistory(track: Track) {
        searchHistoryStorage.addTrack(track)
    }

    override fun getSearchHistory(): List<Track> {
        return searchHistoryStorage.getTracks()
    }

    override fun clearSearchHistory() {
        searchHistoryStorage.clearHistory()
    }
}