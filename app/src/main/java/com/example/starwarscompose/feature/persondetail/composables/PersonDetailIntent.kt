package com.example.starwarscompose.feature.persondetail.composables

sealed interface PersonDetailIntent {
    data class FilmClicked(val filmUrl: String) : PersonDetailIntent
    object BackClicked : PersonDetailIntent
}
