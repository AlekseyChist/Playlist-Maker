package com.example.playlistmaker

import retrofit2.Call
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
    fun search(@Query("term") term: String, @Query("entity") entity: String = "song"): Call<SearchResponse>
}

object iTunesApiService {
    private const val BASE_URL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApi = retrofit.create(iTunesApi::class.java)

    fun searchTracks(term: String): SearchResponse? {
        val call = iTunesApi.search(term)
        val response = call.execute()
        return if (response.isSuccessful) response.body() else null
    }
}