package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SearchTracksUseCase {
    fun execute(query: String): Flow<List<Track>>
}

class SearchTracksUseCaseImpl(
    private val repository: TrackRepository
) : SearchTracksUseCase {
    override fun execute(query: String): Flow<List<Track>> {
        return if (query.isBlank()) {
            flow { emit(emptyList()) }
        } else {
            repository.searchTracks(query)
        }
    }
}