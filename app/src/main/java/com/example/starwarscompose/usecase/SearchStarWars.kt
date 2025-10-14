package com.example.starwarscompose.usecase

import com.example.starwarscompose.repository.SearchRepository

class SearchStarWars(private val repository: SearchRepository) {

    operator fun invoke(query: String): List<String> {
        return repository.getSearchResults(query)
    }
}