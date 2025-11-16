package com.example.starwarscompose.feature.search.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starwarscompose.feature.search.composables.SearchIntent
import com.example.starwarscompose.feature.search.composables.SearchResultItem
import com.example.starwarscompose.usecase.SearchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val searchUseCase: SearchUseCase) : ViewModel() {

    private val _state = MutableStateFlow(SearchViewState())
    val state: StateFlow<SearchViewState> = _state

    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged ->
                _state.value = _state.value.copy(query = intent.query)

            SearchIntent.SubmitSearch ->
                search(_state.value.query)
        }
    }

    private fun search(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching { searchUseCase(query) }
                .onSuccess { people ->
                    val items = people.map { person ->
                        SearchResultItem(
                            title = person.name,
                            description = "Height: ${person.height}, Birth Year: ${person.birthYear}"
                        )
                    }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        results = items
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
        }
    }
}
