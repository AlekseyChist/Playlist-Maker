package com.example.playlistmaker.domain.usecases.tracks

import com.example.playlistmaker.domain.listeners.TracksConsumer
import com.example.playlistmaker.domain.repositories.TrackRepository

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