package com.example.starwarscompose.feature.persondetail.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.example.starwarscompose.feature.search.composables.AnimationType
import com.example.starwarscompose.navigation.PersonDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PersonDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<PersonDetailRoute>()

    private val _state = MutableStateFlow(
        PersonDetailViewState(
            name = route.name,
            height = route.height,
            hairColor = route.hairColor,
            eyeColor = route.eyeColor,
            birthYear = route.birthYear,
            filmUrls = route.filmUrls,
            animationType = deriveAnimationType(
                eyeColor = route.eyeColor,
                height = route.height,
                hairColor = route.hairColor
            )
        )
    )
    val state: StateFlow<PersonDetailViewState> = _state

    private fun deriveAnimationType(
        eyeColor: String,
        height: String,
        hairColor: String
    ): AnimationType {
        val heightValue = height.toIntOrNull() ?: 0

        return when {
            eyeColor.contains("yellow", ignoreCase = true) -> AnimationType.PULSE_RED
            eyeColor.contains("blue", ignoreCase = true) -> AnimationType.ROTATE_BLUE
            heightValue > 200 -> AnimationType.SCALE_GREEN
            hairColor.equals("n/a", ignoreCase = true) ||
                hairColor.equals("none", ignoreCase = true) -> AnimationType.ORBIT_PURPLE
            else -> AnimationType.FADE_GRAY
        }
    }
}
