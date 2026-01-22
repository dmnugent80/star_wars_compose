package com.example.starwarscompose.integration

import app.cash.turbine.test
import com.example.starwarscompose.api.PeopleResponse
import com.example.starwarscompose.api.PeopleSearchApi
import com.example.starwarscompose.api.PersonProperties
import com.example.starwarscompose.api.PersonResult
import com.example.starwarscompose.feature.search.composables.AnimationType
import com.example.starwarscompose.feature.search.composables.SearchIntent
import com.example.starwarscompose.feature.search.viewModel.SearchViewModel
import com.example.starwarscompose.repository.SearchRepositoryImpl
import com.example.starwarscompose.usecase.SearchUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Integration tests for the search flow: ViewModel -> UseCase -> Repository -> API
 * Uses MockK only for the API layer to verify the entire flow works correctly.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchFlowIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var api: PeopleSearchApi
    private lateinit var repository: SearchRepositoryImpl
    private lateinit var useCase: SearchUseCase
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        repository = SearchRepositoryImpl(api)
        useCase = SearchUseCase(repository)
        viewModel = SearchViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search flow from intent to state update`() = runTest {
        val apiResponse = PeopleResponse(
            message = "ok",
            result = listOf(
                PersonResult(
                    uid = "1",
                    properties = PersonProperties(
                        name = "Luke Skywalker",
                        height = "172",
                        hairColor = "blond",
                        eyeColor = "blue",
                        birthYear = "19BBY",
                        films = listOf("https://swapi.tech/api/films/1")
                    )
                ),
                PersonResult(
                    uid = "4",
                    properties = PersonProperties(
                        name = "Darth Vader",
                        height = "202",
                        hairColor = "none",
                        eyeColor = "yellow",
                        birthYear = "41.9BBY",
                        films = listOf("https://swapi.tech/api/films/1", "https://swapi.tech/api/films/2")
                    )
                )
            )
        )

        coEvery { api.searchPeople("skywalker") } returns apiResponse

        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertEquals("", initialState.query)
            assertTrue(initialState.results.isEmpty())
            assertFalse(initialState.isLoading)

            // Query changed
            viewModel.handleIntent(SearchIntent.QueryChanged("skywalker"))
            val queryState = awaitItem()
            assertEquals("skywalker", queryState.query)

            // Submit search
            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)

            // Results state
            val resultState = awaitItem()
            assertFalse(resultState.isLoading)
            assertNull(resultState.error)
            assertEquals(2, resultState.results.size)

            // Verify first result (Luke)
            val luke = resultState.results[0]
            assertEquals("Luke Skywalker", luke.name)
            assertEquals("172", luke.height)
            assertEquals("blue", luke.eyeColor)
            assertEquals(AnimationType.ROTATE_BLUE, luke.animationType)

            // Verify second result (Vader)
            val vader = resultState.results[1]
            assertEquals("Darth Vader", vader.name)
            assertEquals("202", vader.height)
            assertEquals("yellow", vader.eyeColor)
            assertEquals(AnimationType.PULSE_RED, vader.animationType)
        }
    }

    @Test
    fun `search flow handles API error gracefully`() = runTest {
        coEvery { api.searchPeople("error") } throws RuntimeException("Network error")

        viewModel.state.test {
            awaitItem() // Initial state

            viewModel.handleIntent(SearchIntent.QueryChanged("error"))
            awaitItem() // Query changed

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // Loading state

            // Result state with empty results (repository catches exception)
            val resultState = awaitItem()
            assertFalse(resultState.isLoading)
            assertTrue(resultState.results.isEmpty())
            // Note: Repository catches the exception and returns empty list
        }
    }

    @Test
    fun `search flow with empty API response`() = runTest {
        val apiResponse = PeopleResponse(
            message = "ok",
            result = emptyList()
        )

        coEvery { api.searchPeople("nonexistent") } returns apiResponse

        viewModel.state.test {
            awaitItem() // Initial state

            viewModel.handleIntent(SearchIntent.QueryChanged("nonexistent"))
            awaitItem() // Query changed

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // Loading state

            val resultState = awaitItem()
            assertFalse(resultState.isLoading)
            assertTrue(resultState.results.isEmpty())
            assertNull(resultState.error)
        }
    }

    @Test
    fun `search flow correctly propagates all person data`() = runTest {
        val apiResponse = PeopleResponse(
            message = "ok",
            result = listOf(
                PersonResult(
                    uid = "3",
                    properties = PersonProperties(
                        name = "R2-D2",
                        height = "96",
                        hairColor = "n/a",
                        eyeColor = "red",
                        birthYear = "33BBY",
                        films = listOf(
                            "https://swapi.tech/api/films/1",
                            "https://swapi.tech/api/films/2",
                            "https://swapi.tech/api/films/3"
                        )
                    )
                )
            )
        )

        coEvery { api.searchPeople("r2") } returns apiResponse

        viewModel.state.test {
            awaitItem() // Initial state

            viewModel.handleIntent(SearchIntent.QueryChanged("r2"))
            awaitItem()

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // Loading

            val resultState = awaitItem()
            val r2d2 = resultState.results[0]

            assertEquals("R2-D2", r2d2.name)
            assertEquals("96", r2d2.height)
            assertEquals("n/a", r2d2.hairColor)
            assertEquals("red", r2d2.eyeColor)
            assertEquals("33BBY", r2d2.birthYear)
            assertEquals(3, r2d2.filmUrls.size)
            assertEquals(AnimationType.ORBIT_PURPLE, r2d2.animationType)
        }
    }
}
