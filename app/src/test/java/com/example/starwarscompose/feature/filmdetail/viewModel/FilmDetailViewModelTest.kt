package com.example.starwarscompose.feature.filmdetail.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.example.starwarscompose.fake.FakeFilmRepository
import com.example.starwarscompose.fake.TestData
import com.example.starwarscompose.feature.filmdetail.composables.FilmDetailIntent
import com.example.starwarscompose.navigation.FilmDetailRoute
import com.example.starwarscompose.usecase.GetFilmUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class FilmDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeFilmRepository: FakeFilmRepository
    private lateinit var getFilmUseCase: GetFilmUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    private val testFilmUrl = "https://swapi.tech/api/films/1"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeFilmRepository = FakeFilmRepository()
        getFilmUseCase = GetFilmUseCase(fakeFilmRepository)
        savedStateHandle = mockk()

        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<FilmDetailRoute>() } returns FilmDetailRoute(filmUrl = testFilmUrl)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel(): FilmDetailViewModel {
        return FilmDetailViewModel(savedStateHandle, getFilmUseCase)
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeFilmRepository.film = TestData.aNewHope

        val viewModel = createViewModel()

        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `loadFilm success updates state with film`() = runTest {
        fakeFilmRepository.film = TestData.aNewHope

        val viewModel = createViewModel()

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertNull(loadedState.error)
            assertEquals("A New Hope", loadedState.film?.title)
            assertEquals("George Lucas", loadedState.film?.director)
        }
    }

    @Test
    fun `loadFilm with null film shows error`() = runTest {
        fakeFilmRepository.film = null

        val viewModel = createViewModel()

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Film not found", errorState.error)
            assertNull(errorState.film)
        }
    }

    @Test
    fun `loadFilm failure shows error message`() = runTest {
        fakeFilmRepository.shouldThrowError = true
        fakeFilmRepository.errorMessage = "Network error"

        val viewModel = createViewModel()

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Network error", errorState.error)
        }
    }

    @Test
    fun `Retry intent reloads film`() = runTest {
        fakeFilmRepository.shouldThrowError = true
        fakeFilmRepository.errorMessage = "Network error"

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Initial loading
            testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem()
            assertEquals("Network error", errorState.error)

            // Now fix the error and retry
            fakeFilmRepository.shouldThrowError = false
            fakeFilmRepository.film = TestData.aNewHope

            viewModel.handleIntent(FilmDetailIntent.Retry)
            testDispatcher.scheduler.advanceUntilIdle()

            val retryLoadingState = awaitItem()
            assertTrue(retryLoadingState.isLoading)
            assertNull(retryLoadingState.error)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals("A New Hope", successState.film?.title)
        }
    }

    @Test
    fun `film details are correctly mapped`() = runTest {
        fakeFilmRepository.film = TestData.aNewHope

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem() // Initial loading
            testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            val film = loadedState.film!!

            assertEquals("A New Hope", film.title)
            assertEquals("George Lucas", film.director)
            assertEquals("Gary Kurtz, Rick McCallum", film.producer)
            assertEquals("1977-05-25", film.releaseDate)
            assertEquals("It is a period of civil war...", film.openingCrawl)
            assertEquals("https://swapi.tech/api/films/1", film.url)
        }
    }
}
