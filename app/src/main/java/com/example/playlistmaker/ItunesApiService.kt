package com.example.playlistmaker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    @GET("/search")
    suspend fun search(@Query("term") term: String, @Query("entity") entity: String = "song"): SearchResponse
}

data class SearchResponse(
    val resultCount: Int,
    val results: List<Track>
)

object iTunesApiService {
    private const val BASE_URL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApi = retrofit.create(iTunesApi::class.java)

    suspend fun searchTracks(term: String): SearchResponse {
        return iTunesApi.search(term)
    }
}