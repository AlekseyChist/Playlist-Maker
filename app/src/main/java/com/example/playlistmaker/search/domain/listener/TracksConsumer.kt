package com.example.playlistmaker.search.domain.listener

import com.example.playlistmaker.search.domain.model.Track


interface TracksConsumer {
    fun consume(tracks: List<Track>)
}
