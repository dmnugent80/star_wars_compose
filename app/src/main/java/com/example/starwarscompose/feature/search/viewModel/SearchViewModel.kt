package com.example.starwarscompose.feature.search.viewModel

import androidx.lifecycle.ViewModel
import com.example.starwarscompose.usecase.SearchStarWars

class SearchViewModel(private val search: SearchStarWars) : ViewModel() {

    fun search(query: String): List<String> {
        return search(query)
    }
}