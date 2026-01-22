package com.example.starwarscompose.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.starwarscompose.feature.filmdetail.composables.FilmDetailIntent
import com.example.starwarscompose.feature.filmdetail.composables.FilmDetailScreen
import com.example.starwarscompose.feature.filmdetail.viewModel.FilmDetailViewModel
import com.example.starwarscompose.feature.persondetail.composables.PersonDetailIntent
import com.example.starwarscompose.feature.persondetail.composables.PersonDetailScreen
import com.example.starwarscompose.feature.persondetail.viewModel.PersonDetailViewModel
import com.example.starwarscompose.feature.search.composables.SearchIntent
import com.example.starwarscompose.feature.search.composables.SearchScreen
import com.example.starwarscompose.feature.search.viewModel.SearchViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StarWarsNavHost() {
    val navController = rememberNavController()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = SearchRoute
        ) {
            composable<SearchRoute> {
                val viewModel: SearchViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                SearchScreen(
                    state = state,
                    animatedVisibilityScope = this@composable,
                    onIntent = { intent ->
                        when (intent) {
                            is SearchIntent.PersonClicked -> {
                                val item = intent.item
                                navController.navigate(
                                    PersonDetailRoute(
                                        name = item.name,
                                        height = item.height,
                                        hairColor = item.hairColor,
                                        eyeColor = item.eyeColor,
                                        birthYear = item.birthYear,
                                        filmUrls = item.filmUrls
                                    )
                                )
                            }
                            else -> viewModel.handleIntent(intent)
                        }
                    }
                )
            }

            composable<PersonDetailRoute> {
                val viewModel: PersonDetailViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                PersonDetailScreen(
                    state = state,
                    animatedVisibilityScope = this@composable,
                    onIntent = { intent ->
                        when (intent) {
                            is PersonDetailIntent.FilmClicked -> {
                                navController.navigate(FilmDetailRoute(filmUrl = intent.filmUrl))
                            }
                            PersonDetailIntent.BackClicked -> {
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }

            composable<FilmDetailRoute> {
                val viewModel: FilmDetailViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                FilmDetailScreen(
                    state = state,
                    onIntent = { intent ->
                        when (intent) {
                            FilmDetailIntent.BackClicked -> {
                                navController.popBackStack()
                            }
                            FilmDetailIntent.Retry -> viewModel.handleIntent(intent)
                        }
                    }
                )
            }
        }
    }
}
