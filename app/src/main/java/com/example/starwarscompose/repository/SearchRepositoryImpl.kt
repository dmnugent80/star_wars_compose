package com.example.starwarscompose.repository

import com.example.starwarscompose.api.PeopleSearchApi
import com.example.starwarscompose.model.Person
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: PeopleSearchApi
) : SearchRepository {

    override suspend fun getSearchResults(query: String): List<Person> {
        return try {
            val response = api.searchPeople(query)
            response.result?.map { personResult ->
                val props = personResult.properties
                Person(
                    name = props.name,
                    height = props.height,
                    hairColor = props.hairColor,
                    eyeColor = props.eyeColor,
                    birthYear = props.birthYear,
                    films = props.films ?: emptyList()
                )
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
