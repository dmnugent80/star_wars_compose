package com.example.starwarscompose.feature.search.viewModel

import com.example.starwarscompose.model.Person

data class SearchViewState(
    val query: String = "",
    val results: List<Person> = emptyList(),
    val isLoading: Boolean = false,
    val onSearch: (String) -> Unit = {}
)