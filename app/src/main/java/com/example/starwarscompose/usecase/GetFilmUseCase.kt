package com.example.starwarscompose.usecase

import com.example.starwarscompose.repository.FilmRepository
import javax.inject.Inject

class GetFilmUseCase @Inject constructor(
    private val repository: FilmRepository
) {
    suspend operator fun invoke(url: String) = repository.getFilm(url)
}
