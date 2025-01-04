package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.listener.TracksConsumer
import com.example.playlistmaker.search.domain.repository.TrackRepository

interface SearchTracksUseCase {
    fun execute(query: String, consumer: TracksConsumer)
}

class SearchTracksUseCaseImpl(
    private val repository: TrackRepository
) : SearchTracksUseCase {
    override fun execute(query: String, consumer: TracksConsumer) {
        if (query.isBlank()) {
            consumer.consume(emptyList())
        } else {
            repository.searchTracks(query, consumer)
        }
    }
}