package com.example.starwarscompose.repository

import com.example.starwarscompose.model.Person

interface SearchRepository {
    suspend fun getSearchResults(query: String): List<Person>
}
