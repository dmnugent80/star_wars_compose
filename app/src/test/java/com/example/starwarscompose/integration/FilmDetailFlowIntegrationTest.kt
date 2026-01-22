package com.example.starwarscompose.integration

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.example.starwarscompose.api.FilmApi
import com.example.starwarscompose.api.FilmProperties
import com.example.starwarscompose.api.FilmResponse
import com.example.starwarscompose.api.FilmResult
import com.example.starwarscompose.feature.filmdetail.composables.FilmDetailIntent
import com.example.starwarscompose.feature.filmdetail.viewModel.FilmDetailViewModel
import com.example.starwarscompose.navigation.FilmDetailRoute
import com.example.starwarscompose.repository.FilmRepositoryImpl
import com.example.starwarscompose.usecase.GetFilmUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
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
 * Integration tests for the film detail flow: ViewModel -> UseCase -> Repository -> API
 * Uses MockK only for the API layer to verify the entire flow works correctly.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FilmDetailFlowIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var api: FilmApi
    private lateinit var repository: FilmRepositoryImpl
    private lateinit var useCase: GetFilmUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    private val testFilmUrl = "https://swapi.tech/api/films/1"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic("androidx.navigation.SavedStateHandleKt__SavedStateHandleKt")
        api = mockk()
        repository = FilmRepositoryImpl(api)
        useCase = GetFilmUseCase(repository)
        savedStateHandle = mockk(relaxed = true)
        every { savedStateHandle.toRoute<FilmDetailRoute>() } returns FilmDetailRoute(filmUrl = testFilmUrl)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel(): FilmDetailViewModel {
        return FilmDetailViewModel(savedStateHandle, useCase)
    }

    @Test
    fun `film detail loads and displays correctly`() = runTest {
        val apiResponse = FilmResponse(
            message = "ok",
            result = FilmResult(
                uid = "1",
                properties = FilmProperties(
                    title = "A New Hope",
                    director = "George Lucas",
                    producer = "Gary Kurtz, Rick McCallum",
                    releaseDate = "1977-05-25",
                    openingCrawl = "It is a period of civil war...",
                    url = testFilmUrl
                )
            )
        )

        coEvery { api.getFilm(testFilmUrl) } returns apiResponse

        val viewModel = createViewModel()

        viewModel.state.test {
            // Initial loading state
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            assertNull(initialState.film)
            assertNull(initialState.error)

            testDispatcher.scheduler.advanceUntilIdle()

            // Loaded state
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertNull(loadedState.error)

            val film = loadedState.film!!
            assertEquals("A New Hope", film.title)
            assertEquals("George Lucas", film.director)
            assertEquals("Gary Kurtz, Rick McCallum", film.producer)
            assertEquals("1977-05-25", film.releaseDate)
            assertEquals("It is a period of civil war...", film.openingCrawl)
            assertEquals(testFilmUrl, film.url)
        }
    }

    @Test
    fun `film detail retry works after failure`() = runTest {
        // First call fails
        coEvery { api.getFilm(testFilmUrl) } throws RuntimeException("Network error")

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Initial loading
            testDispatcher.scheduler.advanceUntilIdle()

            // Error state (null from repository due to exception)
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Film not found", errorState.error)

            // Setup success response for retry
            val successResponse = FilmResponse(
                message = "ok",
                result = FilmResult(
                    uid = "1",
                    properties = FilmProperties(
                        title = "A New Hope",
                        director = "George Lucas",
                        producer = "Gary Kurtz, Rick McCallum",
                        releaseDate = "1977-05-25",
                        openingCrawl = "It is a period of civil war...",
                        url = testFilmUrl
                    )
                )
            )
            coEvery { api.getFilm(testFilmUrl) } returns successResponse

            // Retry
            viewModel.handleIntent(FilmDetailIntent.Retry)
            testDispatcher.scheduler.advanceUntilIdle()

            // Loading state during retry
            val retryLoadingState = awaitItem()
            assertTrue(retryLoadingState.isLoading)
            assertNull(retryLoadingState.error)

            // Success state after retry
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertNull(successState.error)
            assertEquals("A New Hope", successState.film?.title)
        }
    }

    @Test
    fun `film detail handles not found response`() = runTest {
        val apiResponse = FilmResponse(
            message = "not found",
            result = null
        )

        coEvery { api.getFilm(testFilmUrl) } returns apiResponse

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Initial loading
            testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Film not found", errorState.error)
            assertNull(errorState.film)
        }
    }

    @Test
    fun `film detail flow with Empire Strikes Back`() = runTest {
        val filmUrl = "https://swapi.tech/api/films/2"

        val handle = mockk<SavedStateHandle>(relaxed = true)
        every { handle.toRoute<FilmDetailRoute>() } returns FilmDetailRoute(filmUrl = filmUrl)

        val apiResponse = FilmResponse(
            message = "ok",
            result = FilmResult(
                uid = "2",
                properties = FilmProperties(
                    title = "The Empire Strikes Back",
                    director = "Irvin Kershner",
                    producer = "Gary Kurtz, Rick McCallum",
                    releaseDate = "1980-05-17",
                    openingCrawl = "It is a dark time for the Rebellion...",
                    url = filmUrl
                )
            )
        )

        coEvery { api.getFilm(filmUrl) } returns apiResponse

        val viewModel = FilmDetailViewModel(handle, useCase)

        viewModel.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val loaded = awaitItem()
            assertEquals("The Empire Strikes Back", loaded.film?.title)
        }
    }

}
