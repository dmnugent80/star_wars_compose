package com.example.starwarscompose.fake

import com.example.starwarscompose.model.Film
import com.example.starwarscompose.repository.FilmRepository

class FakeFilmRepository : FilmRepository {
    var film: Film? = null
    var shouldThrowError: Boolean = false
    var errorMessage: String = "Film fetch failed"

    override suspend fun getFilm(url: String): Film? {
        if (shouldThrowError) throw Exception(errorMessage)
        return film
    }
}
