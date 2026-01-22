package com.example.starwarscompose.feature.filmdetail.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.starwarscompose.feature.filmdetail.composables.FilmDetailIntent
import com.example.starwarscompose.navigation.FilmDetailRoute
import com.example.starwarscompose.usecase.GetFilmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFilmUseCase: GetFilmUseCase
) : ViewModel() {

    private val route = savedStateHandle.toRoute<FilmDetailRoute>()

    private val _state = MutableStateFlow(FilmDetailViewState())
    val state: StateFlow<FilmDetailViewState> = _state

    init {
        loadFilm()
    }

    fun handleIntent(intent: FilmDetailIntent) {
        when (intent) {
            FilmDetailIntent.BackClicked -> { /* Handled by navigation */ }
            FilmDetailIntent.Retry -> loadFilm()
        }
    }

    private fun loadFilm() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching { getFilmUseCase(route.filmUrl) }
                .onSuccess { film ->
                    if (film != null) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            film = film
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Film not found"
                        )
                    }
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
