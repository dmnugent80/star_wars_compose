package com.example.starwarscompose.usecase

import com.example.starwarscompose.repository.SearchRepository

class SearchUseCase(private val repository: SearchRepository) {

    suspend operator fun invoke(query: String) = repository.getSearchResults(query)
}
