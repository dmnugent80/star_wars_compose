package com.example.starwarscompose.feature.persondetail.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.starwarscompose.feature.search.composables.AnimationType
import com.example.starwarscompose.navigation.PersonDetailRoute
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PersonDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createMockedSavedStateHandle(
        name: String = "Test Person",
        height: String = "180",
        hairColor: String = "brown",
        eyeColor: String = "brown",
        birthYear: String = "20BBY",
        filmUrls: List<String> = emptyList()
    ): SavedStateHandle {
        val savedStateHandle = mockk<SavedStateHandle>()
        every { savedStateHandle.toRoute<PersonDetailRoute>() } returns PersonDetailRoute(
            name = name,
            height = height,
            hairColor = hairColor,
            eyeColor = eyeColor,
            birthYear = birthYear,
            filmUrls = filmUrls
        )
        return savedStateHandle
    }

    @Test
    fun `state populated from SavedStateHandle`() = runTest {
        val savedStateHandle = createMockedSavedStateHandle(
            name = "Luke Skywalker",
            height = "172",
            hairColor = "blond",
            eyeColor = "blue",
            birthYear = "19BBY",
            filmUrls = listOf("https://swapi.tech/api/films/1")
        )

        val viewModel = PersonDetailViewModel(savedStateHandle)
        val state = viewModel.state.value

        assertEquals("Luke Skywalker", state.name)
        assertEquals("172", state.height)
        assertEquals("blond", state.hairColor)
        assertEquals("blue", state.eyeColor)
        assertEquals("19BBY", state.birthYear)
        assertEquals(listOf("https://swapi.tech/api/films/1"), state.filmUrls)
    }

    @Test
    fun `animation type PULSE_RED for yellow eyes`() = runTest {
        val savedStateHandle = createMockedSavedStateHandle(
            name = "Darth Vader",
            eyeColor = "yellow",
            height = "202",
            hairColor = "none"
        )

        val viewModel = PersonDetailViewModel(savedStateHandle)

        assertEquals(AnimationType.PULSE_RED, viewModel.state.value.animationType)
    }

    @Test
    fun `animation type ROTATE_BLUE for blue eyes`() = runTest {
        val savedStateHandle = createMockedSavedStateHandle(
            name = "Luke Skywalker",
            eyeColor = "blue",
            height = "172",
            hairColor = "blond"
        )

        val viewModel = PersonDetailViewModel(savedStateHandle)

        assertEquals(AnimationType.ROTATE_BLUE, viewModel.state.value.animationType)
    }

    @Test
    fun `animation type SCALE_GREEN for height over 200`() = runTest {
        val savedStateHandle = createMockedSavedStateHandle(
            name = "Chewbacca",
            eyeColor = "brown",
            height = "228",
            hairColor = "brown"
        )

        val viewModel = PersonDetailViewModel(savedStateHandle)

        assertEquals(AnimationType.SCALE_GREEN, viewModel.state.value.animationType)
    }

    @Test
    fun `animation type ORBIT_PURPLE for no hair with n-a`() = runTest {
        val savedStateHandle = createMockedSavedStateHandle(
            name = "R2-D2",
            eyeColor = "red",
            height = "96",
            hairColor = "n/a"
        )

        val viewModel = PersonDetailViewModel(savedStateHandle)

        assertEquals(AnimationType.ORBIT_PURPLE, viewModel.state.value.animationType)
    }

    @Test
    fun `animation type ORBIT_PURPLE for no hair with none`() = runTest {
        val savedStateHandle = createMockedSavedStateHandle(
            name = "Some Droid",
            eyeColor = "red",
            height = "100",
            hairColor = "none"
        )

        val viewModel = PersonDetailViewModel(savedStateHandle)

        assertEquals(AnimationType.ORBIT_PURPLE, viewModel.state.value.animationType)
    }

    @Test
    fun `animation type FADE_GRAY for default case`() = runTest {
        val savedStateHandle = createMockedSavedStateHandle(
            name = "Ordinary Person",
            eyeColor = "brown",
            height = "175",
            hairColor = "brown"
        )

        val viewModel = PersonDetailViewModel(savedStateHandle)

        assertEquals(AnimationType.FADE_GRAY, viewModel.state.value.animationType)
    }

    @Test
    fun `yellow eyes takes precedence over tall height`() = runTest {
        // Character has yellow eyes AND is tall (> 200)
        // Yellow eyes should take precedence
        val savedStateHandle = createMockedSavedStateHandle(
            name = "Tall Yellow Eyes",
            eyeColor = "yellow",
            height = "220",
            hairColor = "brown"
        )

        val viewModel = PersonDetailViewModel(savedStateHandle)

        assertEquals(AnimationType.PULSE_RED, viewModel.state.value.animationType)
    }

    @Test
    fun `blue eyes takes precedence over no hair`() = runTest {
        // Character has blue eyes AND no hair
        // Blue eyes should take precedence
        val savedStateHandle = createMockedSavedStateHandle(
            name = "Blue Eyes No Hair",
            eyeColor = "blue",
            height = "170",
            hairColor = "none"
        )

        val viewModel = PersonDetailViewModel(savedStateHandle)

        assertEquals(AnimationType.ROTATE_BLUE, viewModel.state.value.animationType)
    }

    @Test
    fun `height over 200 takes precedence over no hair`() = runTest {
        // Character is tall AND has no hair
        // Tall should take precedence over no hair
        val savedStateHandle = createMockedSavedStateHandle(
            name = "Tall No Hair",
            eyeColor = "brown",
            height = "210",
            hairColor = "n/a"
        )

        val viewModel = PersonDetailViewModel(savedStateHandle)

        assertEquals(AnimationType.SCALE_GREEN, viewModel.state.value.animationType)
    }

    @Test
    fun `invalid height string defaults to 0`() = runTest {
        val savedStateHandle = createMockedSavedStateHandle(
            name = "Unknown Height",
            eyeColor = "brown",
            height = "unknown",
            hairColor = "brown"
        )

        val viewModel = PersonDetailViewModel(savedStateHandle)

        // With invalid height (defaults to 0), not tall, brown eyes, has hair -> FADE_GRAY
        assertEquals(AnimationType.FADE_GRAY, viewModel.state.value.animationType)
    }
}
