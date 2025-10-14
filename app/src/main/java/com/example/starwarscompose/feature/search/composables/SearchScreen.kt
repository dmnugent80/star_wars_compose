package com.example.starwarscompose.feature.search.composables

import androidx.compose.runtime.Composable

@Composable
fun SearchScreen(
    state: SearchScreenState
) {


}

data class SearchScreenState(
    val title: String,
    val searchResultList: List<SearchResultItem>,
)

data class SearchResultItem(
    val title: String,
    val description: String,
)