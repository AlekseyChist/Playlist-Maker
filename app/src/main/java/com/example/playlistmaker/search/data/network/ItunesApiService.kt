package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.SearchResponseDto
import com.example.playlistmaker.search.domain.model.Track
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class SearchResponse(
    val resultCount: Int,
    val results: List<Track>
)

interface iTunesApi {
    @GET("/search")
    suspend fun search(
        @Query("term") term: String,
        @Query("entity") entity: String = ENTITY_SONG
    ): SearchResponseDto

    companion object {
        const val ENTITY_SONG = "song"
    }
}

object RetrofitClient {
    private const val BASE_URL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val iTunesApi = retrofit.create(iTunesApi::class.java)

    suspend fun searchTracks(term: String): SearchResponseDto {
        return iTunesApi.search(term)
    }
}