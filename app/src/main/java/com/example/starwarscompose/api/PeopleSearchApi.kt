package com.example.starwarscompose.api

import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Query

interface PeopleSearchApi {
    @GET("people/")
    suspend fun searchPeople(@Query("name") query: String): PeopleResponse
}

data class PeopleResponse(
    val message: String,
    val result: List<PersonResult>?
)

data class PersonResult(
    val uid: String,
    val properties: PersonProperties
)

data class PersonProperties(
    val name: String,
    val height: String,
    @Json(name = "hair_color") val hairColor: String,
    @Json(name = "eye_color") val eyeColor: String,
    @Json(name = "birth_year") val birthYear: String,
    val films: List<String>?
)