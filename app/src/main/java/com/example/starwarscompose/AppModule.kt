package com.example.starwarscompose

import com.example.starwarscompose.feature.search.viewModel.SearchViewModel
import com.example.starwarscompose.usecase.SearchStarWars
import org.koin.dsl.module
import  org.koin.core.module.dsl.viewModel

val appModule = module {

    // Use Case
    single { SearchStarWars(get()) }

    // ViewModel
    viewModel { SearchViewModel(get()) }
}