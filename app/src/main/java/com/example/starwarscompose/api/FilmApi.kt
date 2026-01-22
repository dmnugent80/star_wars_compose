package com.example.starwarscompose.api

import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Url

interface FilmApi {
    @GET
    suspend fun getFilm(@Url url: String): FilmResponse
}

data class FilmResponse(
    val message: String,
    val result: FilmResult?
)

data class FilmResult(
    val uid: String,
    val properties: FilmProperties
)

data class FilmProperties(
    val title: String,
    val director: String,
    val producer: String,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "opening_crawl") val openingCrawl: String,
    val url: String
)
