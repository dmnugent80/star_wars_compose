package com.example.starwarscompose.feature.filmdetail.composables

sealed interface FilmDetailIntent {
    object BackClicked : FilmDetailIntent
    object Retry : FilmDetailIntent
}
