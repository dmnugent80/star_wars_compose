package com.example.starwarscompose.fake

import com.example.starwarscompose.model.Person
import com.example.starwarscompose.repository.SearchRepository

class FakeSearchRepository : SearchRepository {
    var searchResults: List<Person> = emptyList()
    var shouldThrowError: Boolean = false
    var errorMessage: String = "Search failed"

    override suspend fun getSearchResults(query: String): List<Person> {
        if (shouldThrowError) throw Exception(errorMessage)
        return searchResults
    }
}
