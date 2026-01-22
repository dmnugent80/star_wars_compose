package com.example.starwarscompose.repository

import com.example.starwarscompose.api.FilmApi
import com.example.starwarscompose.model.Film
import javax.inject.Inject

class FilmRepositoryImpl @Inject constructor(
    private val api: FilmApi
) : FilmRepository {

    override suspend fun getFilm(url: String): Film? {
        return try {
            val response = api.getFilm(url)
            response.result?.let { filmResult ->
                val props = filmResult.properties
                Film(
                    title = props.title,
                    director = props.director,
                    producer = props.producer,
                    releaseDate = props.releaseDate,
                    openingCrawl = props.openingCrawl,
                    url = props.url
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
