package com.example.playlistmaker.data.repositories

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.playlistmaker.data.dto.SearchResponseDto
import com.example.playlistmaker.data.mappers.TrackMapper
import com.example.playlistmaker.data.network.iTunesApi
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.domain.listeners.TracksConsumer
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repositories.TrackRepository

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