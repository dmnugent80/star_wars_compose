package com.example.starwarscompose

import com.example.starwarscompose.api.PeopleSearchApi
import com.example.starwarscompose.feature.search.viewModel.SearchViewModel
import com.example.starwarscompose.repository.SearchRepository
import com.example.starwarscompose.repository.SearchRepositoryImpl
import com.example.starwarscompose.usecase.SearchUseCase
import com.squareup.moshi.Moshi
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

val appModule = module {

    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // API
    single {
        Retrofit.Builder()
            .baseUrl("https://swapi.dev/api/") // Star Wars API base URL
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
            .create(PeopleSearchApi::class.java)
    }

    // Repository
    single<SearchRepository> { SearchRepositoryImpl(get()) }

    // UseCase
    single { SearchUseCase(get()) }

    // ViewModel
    viewModel { SearchViewModel(get()) }
}