package com.example.starwarscompose.usecase

import com.example.starwarscompose.repository.SearchRepository
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String) = repository.getSearchResults(query)
}
