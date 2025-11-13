package com.example.starwarscompose.feature.search.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starwarscompose.feature.search.composables.SearchResultItem
import com.example.starwarscompose.feature.search.composables.SearchScreenState
import com.example.starwarscompose.usecase.SearchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val searchUseCase: SearchUseCase) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SearchScreenState(
            query = "",
            searchResultList = emptyList(),
            isLoading = false,
            onQueryChanged = { query -> updateQuery(query) },
            onSearch = { query -> performSearch(query) }
        )
    )
    val viewState: StateFlow<SearchScreenState> = _viewState.asStateFlow()

    private fun updateQuery(query: String) {
        _viewState.value = _viewState.value.copy(query = query)
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isLoading = true)

            val results = searchUseCase(query)
            val mappedResults = results.map { person ->
                SearchResultItem(
                    title = person.name,
                    description = "Height: ${person.height}, Birth Year: ${person.birthYear}"
                )
            }

            _viewState.value = _viewState.value.copy(
                searchResultList = mappedResults,
                isLoading = false
            )
        }
    }
}
