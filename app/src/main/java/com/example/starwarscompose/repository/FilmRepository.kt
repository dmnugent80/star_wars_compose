package com.example.starwarscompose.repository

import com.example.starwarscompose.model.Film

interface FilmRepository {
    suspend fun getFilm(url: String): Film?
}
