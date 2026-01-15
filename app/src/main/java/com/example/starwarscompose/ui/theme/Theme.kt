package com.example.starwarscompose.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = StarWarsYellow,
    secondary = StarWarsGold,
    tertiary = StarWarsMediumGray,
    background = StarWarsBlack,
    surface = StarWarsSurface,
    onPrimary = StarWarsBlack,
    onSecondary = StarWarsBlack,
    onTertiary = StarWarsBlack,
    onBackground = StarWarsLightGray,
    onSurface = StarWarsLightGray
)

@Composable
fun StarWarsComposeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}