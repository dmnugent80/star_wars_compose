package com.example.starwarscompose.navigation

import kotlinx.serialization.Serializable

@Serializable
object SearchRoute

@Serializable
data class PersonDetailRoute(
    val name: String,
    val height: String,
    val hairColor: String,
    val eyeColor: String,
    val birthYear: String,
    val filmUrls: List<String>
)

@Serializable
data class FilmDetailRoute(val filmUrl: String)
