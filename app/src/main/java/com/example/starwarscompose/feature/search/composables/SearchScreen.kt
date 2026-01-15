package com.example.starwarscompose.feature.search.composables

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.starwarscompose.feature.search.viewModel.SearchViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchViewState,
    onIntent: (SearchIntent) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        TopAppBar(
            modifier = Modifier.background(Color.Transparent),
            title = { Text("Star Wars Search") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { newQuery ->
                    onIntent(SearchIntent.QueryChanged(newQuery))
                },
                modifier = Modifier.weight(1f),
                label = { Text("Search People") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onIntent(SearchIntent.SubmitSearch)
                        focusManager.clearFocus()
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    onIntent(SearchIntent.SubmitSearch)
                    focusManager.clearFocus()
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(color = Color(0xFF4CAF50), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.results) { item ->
                SearchResultRow(item)
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            }
        }
    }
}

@Composable
fun SearchResultRow(item: SearchResultItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedIndicator(animationType = item.animationType)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen(
        state = SearchViewState(
            query = "Luke",
            results = listOf(
                SearchResultItem("Luke Skywalker", "Height: 172, Birth Year: 19BBY", AnimationType.ROTATE_BLUE),
                SearchResultItem("Darth Vader", "Height: 202, Birth Year: 41.9BBY", AnimationType.PULSE_RED),
                SearchResultItem("Chewbacca", "Height: 228, Birth Year: 200BBY", AnimationType.SCALE_GREEN),
                SearchResultItem("R2-D2", "Height: 96, Birth Year: 33BBY", AnimationType.ORBIT_PURPLE),
            )
        ),
        onIntent = {},
    )
}


// --- State definitions ---

data class SearchResultItem(
    val title: String,
    val description: String,
    val animationType: AnimationType = AnimationType.FADE_GRAY,
)

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    object SubmitSearch : SearchIntent
}
