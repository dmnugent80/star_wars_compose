package com.example.starwarscompose.di

import com.example.starwarscompose.repository.FilmRepository
import com.example.starwarscompose.repository.FilmRepositoryImpl
import com.example.starwarscompose.repository.SearchRepository
import com.example.starwarscompose.repository.SearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindFilmRepository(
        filmRepositoryImpl: FilmRepositoryImpl
    ): FilmRepository
}
