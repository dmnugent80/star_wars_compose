package com.example.starwarscompose.repository

import com.example.starwarscompose.api.PeopleSearchApi
import com.example.starwarscompose.model.Person

class SearchRepositoryImpl(
    private val api: PeopleSearchApi
) : SearchRepository {

    override suspend fun getSearchResults(query: String): List<Person> {
        return try {
            val response = api.searchPeople(query)
            println("<><><> response $response")
            response.results.map {
                Person(
                    name = it.name,
                    height = it.height,
                    hairColor = it.hairColor,
                    eyeColor = it.eyeColor,
                    birthYear = it.birthYear,
                    films = it.films
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
