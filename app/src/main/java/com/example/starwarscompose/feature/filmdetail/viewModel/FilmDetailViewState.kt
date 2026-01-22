package com.example.starwarscompose.feature.filmdetail.viewModel

import com.example.starwarscompose.model.Film

data class FilmDetailViewState(
    val film: Film? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
