package com.example.starwarscompose.repository

import com.example.starwarscompose.api.PeopleResponse
import com.example.starwarscompose.api.PeopleSearchApi
import com.example.starwarscompose.api.PersonProperties
import com.example.starwarscompose.api.PersonResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchRepositoryImplTest {

    private lateinit var api: PeopleSearchApi
    private lateinit var repository: SearchRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        repository = SearchRepositoryImpl(api)
    }

    @Test
    fun `getSearchResults maps API response to Person list`() = runTest {
        val apiResponse = PeopleResponse(
            message = "ok",
            result = listOf(
                PersonResult(
                    uid = "1",
                    properties = PersonProperties(
                        name = "Luke Skywalker",
                        height = "172",
                        hairColor = "blond",
                        eyeColor = "blue",
                        birthYear = "19BBY",
                        films = listOf("https://swapi.tech/api/films/1")
                    )
                )
            )
        )

        coEvery { api.searchPeople("luke") } returns apiResponse

        val result = repository.getSearchResults("luke")

        assertEquals(1, result.size)
        assertEquals("Luke Skywalker", result[0].name)
    }

    @Test
    fun `getSearchResults returns empty list when result is null`() = runTest {
        val apiResponse = PeopleResponse(
            message = "ok",
            result = null
        )

        coEvery { api.searchPeople("nonexistent") } returns apiResponse

        val result = repository.getSearchResults("nonexistent")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getSearchResults returns empty list on API exception`() = runTest {
        coEvery { api.searchPeople("error") } throws RuntimeException("Network error")

        val result = repository.getSearchResults("error")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getSearchResults correctly maps all person fields`() = runTest {
        val apiResponse = PeopleResponse(
            message = "ok",
            result = listOf(
                PersonResult(
                    uid = "4",
                    properties = PersonProperties(
                        name = "Darth Vader",
                        height = "202",
                        hairColor = "none",
                        eyeColor = "yellow",
                        birthYear = "41.9BBY",
                        films = listOf(
                            "https://swapi.tech/api/films/1",
                            "https://swapi.tech/api/films/2",
                            "https://swapi.tech/api/films/3"
                        )
                    )
                )
            )
        )

        coEvery { api.searchPeople("vader") } returns apiResponse

        val result = repository.getSearchResults("vader")

        assertEquals(1, result.size)
        val person = result[0]
        assertEquals("Darth Vader", person.name)
        assertEquals("202", person.height)
        assertEquals("none", person.hairColor)
        assertEquals("yellow", person.eyeColor)
        assertEquals("41.9BBY", person.birthYear)
        assertEquals(3, person.films.size)
        assertEquals("https://swapi.tech/api/films/1", person.films[0])
    }

    @Test
    fun `getSearchResults handles missing films as empty list`() = runTest {
        val apiResponse = PeopleResponse(
            message = "ok",
            result = listOf(
                PersonResult(
                    uid = "1",
                    properties = PersonProperties(
                        name = "Test Person",
                        height = "180",
                        hairColor = "brown",
                        eyeColor = "brown",
                        birthYear = "50BBY",
                        films = null
                    )
                )
            )
        )

        coEvery { api.searchPeople("test") } returns apiResponse

        val result = repository.getSearchResults("test")

        assertEquals(1, result.size)
        assertTrue(result[0].films.isEmpty())
    }

    @Test
    fun `getSearchResults maps multiple people correctly`() = runTest {
        val apiResponse = PeopleResponse(
            message = "ok",
            result = listOf(
                PersonResult(
                    uid = "1",
                    properties = PersonProperties(
                        name = "Luke Skywalker",
                        height = "172",
                        hairColor = "blond",
                        eyeColor = "blue",
                        birthYear = "19BBY",
                        films = listOf("https://swapi.tech/api/films/1")
                    )
                ),
                PersonResult(
                    uid = "10",
                    properties = PersonProperties(
                        name = "Anakin Skywalker",
                        height = "188",
                        hairColor = "blond",
                        eyeColor = "blue",
                        birthYear = "41.9BBY",
                        films = listOf("https://swapi.tech/api/films/4")
                    )
                )
            )
        )

        coEvery { api.searchPeople("skywalker") } returns apiResponse

        val result = repository.getSearchResults("skywalker")

        assertEquals(2, result.size)
        assertEquals("Luke Skywalker", result[0].name)
        assertEquals("Anakin Skywalker", result[1].name)
    }
}
