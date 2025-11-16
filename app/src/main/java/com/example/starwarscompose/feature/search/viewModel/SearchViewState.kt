package com.example.starwarscompose.feature.search.viewModel

import com.example.starwarscompose.feature.search.composables.SearchResultItem

data class SearchViewState(
    val query: String = "",
    val results: List<SearchResultItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)