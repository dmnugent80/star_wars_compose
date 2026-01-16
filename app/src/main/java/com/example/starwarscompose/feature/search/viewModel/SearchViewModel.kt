package com.example.starwarscompose.feature.search.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starwarscompose.feature.search.composables.AnimationType
import com.example.starwarscompose.feature.search.composables.SearchIntent
import com.example.starwarscompose.feature.search.composables.SearchResultItem
import com.example.starwarscompose.model.Person
import com.example.starwarscompose.usecase.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase
) : ViewModel() {

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

            println("<><><> hello 1")
            runCatching { searchUseCase(query) }
                .onSuccess { people ->
                    val items = people.map { person ->

                        println("<><><> hello  person: " + person.name)
                        SearchResultItem(
                            title = person.name,
                            description = "Height: ${person.height}, Birth Year: ${person.birthYear}",
                            animationType = deriveAnimationType(person)
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

    private fun deriveAnimationType(person: Person): AnimationType {
        val height = person.height.toIntOrNull() ?: 0

        return when {
            person.eyeColor.contains("yellow", ignoreCase = true) -> AnimationType.PULSE_RED
            person.eyeColor.contains("blue", ignoreCase = true) -> AnimationType.ROTATE_BLUE
            height > 200 -> AnimationType.SCALE_GREEN
            person.hairColor.equals("n/a", ignoreCase = true) ||
                person.hairColor.equals("none", ignoreCase = true) -> AnimationType.ORBIT_PURPLE
            else -> AnimationType.FADE_GRAY
        }
    }
}
