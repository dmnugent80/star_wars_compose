package com.example.starwarscompose.feature.search.viewModel

import app.cash.turbine.test
import com.example.starwarscompose.fake.FakeSearchRepository
import com.example.starwarscompose.fake.TestData
import com.example.starwarscompose.feature.search.composables.AnimationType
import com.example.starwarscompose.feature.search.composables.SearchIntent
import com.example.starwarscompose.usecase.SearchUseCase
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeSearchRepository: FakeSearchRepository
    private lateinit var searchUseCase: SearchUseCase
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeSearchRepository = FakeSearchRepository()
        searchUseCase = SearchUseCase(fakeSearchRepository)
        viewModel = SearchViewModel(searchUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() = runTest {
        val state = viewModel.state.value

        assertEquals("", state.query)
        assertTrue(state.results.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `QueryChanged updates query in state`() = runTest {
        viewModel.handleIntent(SearchIntent.QueryChanged("luke"))

        assertEquals("luke", viewModel.state.value.query)
    }

    @Test
    fun `SubmitSearch with results updates state correctly`() = runTest {
        fakeSearchRepository.searchResults = listOf(TestData.lukeSkywalker)

        viewModel.state.test {
            assertEquals(SearchViewState(), awaitItem())

            viewModel.handleIntent(SearchIntent.QueryChanged("luke"))
            assertEquals("luke", awaitItem().query)

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)

            val resultState = awaitItem()
            assertFalse(resultState.isLoading)
            assertEquals(1, resultState.results.size)
            assertEquals("Luke Skywalker", resultState.results[0].name)
        }
    }

    @Test
    fun `SubmitSearch shows loading then results`() = runTest {
        fakeSearchRepository.searchResults = listOf(TestData.lukeSkywalker, TestData.darthVader)

        viewModel.state.test {
            awaitItem() // Initial state

            viewModel.handleIntent(SearchIntent.QueryChanged("skywalker"))
            awaitItem() // Query changed

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue("Should be loading", loadingState.isLoading)

            val finalState = awaitItem()
            assertFalse("Should not be loading after results", finalState.isLoading)
            assertEquals(2, finalState.results.size)
        }
    }

    @Test
    fun `SubmitSearch with empty results shows empty list`() = runTest {
        fakeSearchRepository.searchResults = emptyList()

        viewModel.state.test {
            awaitItem() // Initial state

            viewModel.handleIntent(SearchIntent.QueryChanged("nonexistent"))
            awaitItem() // Query changed

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // Loading state

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertTrue(finalState.results.isEmpty())
            assertNull(finalState.error)
        }
    }

    @Test
    fun `SubmitSearch with error updates error state`() = runTest {
        fakeSearchRepository.shouldThrowError = true
        fakeSearchRepository.errorMessage = "Network error"

        viewModel.state.test {
            awaitItem() // Initial state

            viewModel.handleIntent(SearchIntent.QueryChanged("luke"))
            awaitItem() // Query changed

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // Loading state

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Network error", errorState.error)
        }
    }

    @Test
    fun `SubmitSearch with blank query does not search`() = runTest {
        fakeSearchRepository.searchResults = listOf(TestData.lukeSkywalker)

        viewModel.state.test {
            awaitItem() // Initial state

            viewModel.handleIntent(SearchIntent.QueryChanged(""))

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            // No additional state changes should occur
            expectNoEvents()
        }
    }

    @Test
    fun `animation type derived correctly for yellow eyes`() = runTest {
        fakeSearchRepository.searchResults = listOf(TestData.darthVader)

        viewModel.state.test {
            awaitItem() // Initial

            viewModel.handleIntent(SearchIntent.QueryChanged("vader"))
            awaitItem()

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // Loading

            val resultState = awaitItem()
            assertEquals(AnimationType.PULSE_RED, resultState.results[0].animationType)
        }
    }

    @Test
    fun `animation type derived correctly for blue eyes`() = runTest {
        fakeSearchRepository.searchResults = listOf(TestData.lukeSkywalker)

        viewModel.state.test {
            awaitItem() // Initial

            viewModel.handleIntent(SearchIntent.QueryChanged("luke"))
            awaitItem()

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // Loading

            val resultState = awaitItem()
            assertEquals(AnimationType.ROTATE_BLUE, resultState.results[0].animationType)
        }
    }

    @Test
    fun `animation type derived correctly for tall character`() = runTest {
        fakeSearchRepository.searchResults = listOf(TestData.chewbacca)

        viewModel.state.test {
            awaitItem() // Initial

            viewModel.handleIntent(SearchIntent.QueryChanged("chewbacca"))
            awaitItem()

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // Loading

            val resultState = awaitItem()
            assertEquals(AnimationType.SCALE_GREEN, resultState.results[0].animationType)
        }
    }

    @Test
    fun `animation type derived correctly for no hair`() = runTest {
        // C-3PO has "n/a" for hair color but yellow eyes, so yellow eyes takes precedence
        // Create a person with no hair and non-yellow, non-blue eyes
        val r2d2 = TestData.lukeSkywalker.copy(
            name = "R2-D2",
            hairColor = "n/a",
            eyeColor = "red",
            height = "96"
        )
        fakeSearchRepository.searchResults = listOf(r2d2)

        viewModel.state.test {
            awaitItem() // Initial

            viewModel.handleIntent(SearchIntent.QueryChanged("r2"))
            awaitItem()

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // Loading

            val resultState = awaitItem()
            assertEquals(AnimationType.ORBIT_PURPLE, resultState.results[0].animationType)
        }
    }

    @Test
    fun `animation type default is FADE_GRAY`() = runTest {
        val ordinaryPerson = TestData.lukeSkywalker.copy(
            name = "Ordinary Person",
            eyeColor = "brown",
            hairColor = "brown",
            height = "170"
        )
        fakeSearchRepository.searchResults = listOf(ordinaryPerson)

        viewModel.state.test {
            awaitItem() // Initial

            viewModel.handleIntent(SearchIntent.QueryChanged("ordinary"))
            awaitItem()

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // Loading

            val resultState = awaitItem()
            assertEquals(AnimationType.FADE_GRAY, resultState.results[0].animationType)
        }
    }

    @Test
    fun `search result item contains correct person details`() = runTest {
        fakeSearchRepository.searchResults = listOf(TestData.lukeSkywalker)

        viewModel.state.test {
            awaitItem() // Initial

            viewModel.handleIntent(SearchIntent.QueryChanged("luke"))
            awaitItem()

            viewModel.handleIntent(SearchIntent.SubmitSearch)
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem() // Loading

            val resultState = awaitItem()
            val item = resultState.results[0]

            assertEquals("Luke Skywalker", item.name)
            assertEquals("172", item.height)
            assertEquals("blond", item.hairColor)
            assertEquals("blue", item.eyeColor)
            assertEquals("19BBY", item.birthYear)
            assertEquals(listOf("https://swapi.tech/api/films/1"), item.filmUrls)
        }
    }
}
