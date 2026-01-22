package com.example.starwarscompose.feature.persondetail.composables

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.starwarscompose.feature.persondetail.viewModel.PersonDetailViewState
import com.example.starwarscompose.feature.search.composables.AnimatedIndicator
import com.example.starwarscompose.feature.search.composables.AnimationType
import com.example.starwarscompose.ui.theme.StarWarsComposeTheme

/**
 * Public entry used at runtime, keeps shared elements.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PersonDetailScreen(
    state: PersonDetailViewState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onIntent: (PersonDetailIntent) -> Unit
) {
    PersonDetailScreenContent(
        state = state,
        onIntent = onIntent,
        nameModifier = Modifier.sharedElement(
            state = rememberSharedContentState(key = "name-${state.name}"),
            animatedVisibilityScope = animatedVisibilityScope
        ),
        indicatorModifier = Modifier.sharedElement(
            state = rememberSharedContentState(key = "indicator-${state.name}"),
            animatedVisibilityScope = animatedVisibilityScope
        )
    )
}

/**
 * Inner composable that does NOT require SharedTransitionScope/AnimatedVisibilityScope.
 * Preview (and tests) should call this.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonDetailScreenContent(
    state: PersonDetailViewState,
    onIntent: (PersonDetailIntent) -> Unit,
    nameModifier: Modifier = Modifier,
    indicatorModifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = state.name,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = nameModifier
                )
            },
            navigationIcon = {
                IconButton(onClick = { onIntent(PersonDetailIntent.BackClicked) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AnimatedIndicator(
                    animationType = state.animationType,
                    modifier = indicatorModifier.size(80.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = state.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PersonDetailRow(label = "Height", value = state.height)
            PersonDetailRow(label = "Hair Color", value = state.hairColor)
            PersonDetailRow(label = "Eye Color", value = state.eyeColor)
            PersonDetailRow(label = "Birth Year", value = state.birthYear)

            Spacer(modifier = Modifier.height(24.dp))

            if (state.filmUrls.isNotEmpty()) {
                Text(
                    text = "Films",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    state.filmUrls.forEach { filmUrl ->
                        FilmListItem(
                            filmUrl = filmUrl,
                            onClick = { onIntent(PersonDetailIntent.FilmClicked(filmUrl)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun FilmListItem(
    filmUrl: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = extractFilmNumber(filmUrl),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun extractFilmNumber(url: String): String {
    val filmId = url.trimEnd('/').substringAfterLast('/')
    return "Film $filmId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PersonDetailScreenPreview() {
    StarWarsComposeTheme {
        PersonDetailScreenContent(
            state = PersonDetailViewState(
                name = "Luke Skywalker",
                height = "172",
                hairColor = "Blond",
                eyeColor = "Blue",
                birthYear = "19BBY",
                filmUrls = listOf(
                    "https://swapi.dev/api/films/1/",
                    "https://swapi.dev/api/films/2/",
                    "https://swapi.dev/api/films/3/"
                ),
                animationType = AnimationType.ROTATE_BLUE
            ),
            onIntent = {}
        )
    }
}
