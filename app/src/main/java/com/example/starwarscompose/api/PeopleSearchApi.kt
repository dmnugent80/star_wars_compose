package com.example.starwarscompose.api

import com.example.starwarscompose.model.Person
import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Query

interface PeopleSearchApi {
    @GET("people/")
    suspend fun searchPeople(@Query("search") query: String): PeopleResponse
}

data class PeopleResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PersonResponse>
)

data class PersonResponse(
    val name: String,
    val height: String,
    @Json(name = "hair_color") val hairColor: String,
    @Json(name = "eye_color") val eyeColor: String,
    @Json(name = "birth_year") val birthYear: String,
    val films: List<String>
)