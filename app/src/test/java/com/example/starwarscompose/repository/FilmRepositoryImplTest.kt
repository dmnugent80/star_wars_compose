package com.example.starwarscompose.repository

import com.example.starwarscompose.api.FilmApi
import com.example.starwarscompose.api.FilmProperties
import com.example.starwarscompose.api.FilmResponse
import com.example.starwarscompose.api.FilmResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FilmRepositoryImplTest {

    private lateinit var api: FilmApi
    private lateinit var repository: FilmRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        repository = FilmRepositoryImpl(api)
    }

    @Test
    fun `getFilm maps API response to Film`() = runTest {
        val filmUrl = "https://swapi.tech/api/films/1"
        val apiResponse = FilmResponse(
            message = "ok",
            result = FilmResult(
                uid = "1",
                properties = FilmProperties(
                    title = "A New Hope",
                    director = "George Lucas",
                    producer = "Gary Kurtz, Rick McCallum",
                    releaseDate = "1977-05-25",
                    openingCrawl = "It is a period of civil war...",
                    url = filmUrl
                )
            )
        )

        coEvery { api.getFilm(filmUrl) } returns apiResponse

        val result = repository.getFilm(filmUrl)

        assertEquals("A New Hope", result?.title)
        assertEquals("George Lucas", result?.director)
    }

    @Test
    fun `getFilm returns null when result is null`() = runTest {
        val filmUrl = "https://swapi.tech/api/films/999"
        val apiResponse = FilmResponse(
            message = "not found",
            result = null
        )

        coEvery { api.getFilm(filmUrl) } returns apiResponse

        val result = repository.getFilm(filmUrl)

        assertNull(result)
    }

    @Test
    fun `getFilm returns null on API exception`() = runTest {
        val filmUrl = "https://swapi.tech/api/films/1"

        coEvery { api.getFilm(filmUrl) } throws RuntimeException("Network error")

        val result = repository.getFilm(filmUrl)

        assertNull(result)
    }

    @Test
    fun `getFilm correctly maps all film fields`() = runTest {
        val filmUrl = "https://swapi.tech/api/films/2"
        val apiResponse = FilmResponse(
            message = "ok",
            result = FilmResult(
                uid = "2",
                properties = FilmProperties(
                    title = "The Empire Strikes Back",
                    director = "Irvin Kershner",
                    producer = "Gary Kurtz, Rick McCallum",
                    releaseDate = "1980-05-17",
                    openingCrawl = "It is a dark time for the Rebellion...",
                    url = filmUrl
                )
            )
        )

        coEvery { api.getFilm(filmUrl) } returns apiResponse

        val result = repository.getFilm(filmUrl)

        assertEquals("The Empire Strikes Back", result?.title)
        assertEquals("Irvin Kershner", result?.director)
        assertEquals("Gary Kurtz, Rick McCallum", result?.producer)
        assertEquals("1980-05-17", result?.releaseDate)
        assertEquals("It is a dark time for the Rebellion...", result?.openingCrawl)
        assertEquals(filmUrl, result?.url)
    }

    @Test
    fun `getFilm handles special characters in opening crawl`() = runTest {
        val filmUrl = "https://swapi.tech/api/films/1"
        val longCrawl = """
            It is a period of civil war.
            Rebel spaceships, striking
            from a hidden base, have won
            their first victory against
            the evil Galactic Empire.
        """.trimIndent()

        val apiResponse = FilmResponse(
            message = "ok",
            result = FilmResult(
                uid = "1",
                properties = FilmProperties(
                    title = "A New Hope",
                    director = "George Lucas",
                    producer = "Gary Kurtz, Rick McCallum",
                    releaseDate = "1977-05-25",
                    openingCrawl = longCrawl,
                    url = filmUrl
                )
            )
        )

        coEvery { api.getFilm(filmUrl) } returns apiResponse

        val result = repository.getFilm(filmUrl)

        assertEquals(longCrawl, result?.openingCrawl)
    }
}
