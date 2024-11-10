package com.example.playlistmaker.domain.usecases.tracks

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repositories.TrackRepository

interface SearchTracksUseCase {
    fun execute(query: String): List<Track>
}

class SearchTracksUseCaseImpl(
    private val repository: TrackRepository
) : SearchTracksUseCase {
    override fun execute(query: String): List<Track> {
        return if (query.isBlank()) {
            emptyList()
        } else {
            repository.searchTracks(query)
        }
    }
}