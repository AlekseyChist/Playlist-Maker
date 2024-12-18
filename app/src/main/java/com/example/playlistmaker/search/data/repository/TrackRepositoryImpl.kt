package com.example.playlistmaker.search.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.playlistmaker.search.data.dto.SearchResponseDto
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.data.network.iTunesApi
import com.example.playlistmaker.search.data.storage.SearchHistoryStorage
import com.example.playlistmaker.search.domain.listener.TracksConsumer
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository

class TrackRepositoryImpl(
    private val api: iTunesApi,
    private val searchHistoryStorage: SearchHistoryStorage,
    private val mapper: TrackMapper
) : TrackRepository {

    override fun searchTracks(query: String, consumer: TracksConsumer) {
        api.search(query, "song").enqueue(object : Callback<SearchResponseDto> {
            override fun onResponse(
                call: Call<SearchResponseDto>,
                response: Response<SearchResponseDto>
            ) {
                if (response.isSuccessful) {
                    val results = response.body()?.results ?: emptyList()
                    val tracks = results.map { mapper.mapDtoToDomain(it) }
                    consumer.consume(tracks)
                } else {
                    consumer.consume(emptyList())
                }
            }

            override fun onFailure(
                call: Call<SearchResponseDto>,
                t: Throwable
            ) {
                consumer.consume(emptyList())
            }
        })
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