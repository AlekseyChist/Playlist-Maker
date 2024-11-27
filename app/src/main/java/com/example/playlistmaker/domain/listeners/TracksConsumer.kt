package com.example.playlistmaker.domain.listeners

import com.example.playlistmaker.domain.models.Track


interface TracksConsumer {
    fun consume(tracks: List<Track>)
}
