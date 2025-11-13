package com.example.starwarscompose.feature.search.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchScreenState,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        TopAppBar(
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
                    state.onQueryChanged(newQuery) // just update the query
                },
                modifier = Modifier.weight(1f),
                label = { Text("Search People") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        state.onSearch(state.query) // trigger search
                        focusManager.clearFocus()
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    state.onSearch(state.query)
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
            items(state.searchResultList) { item ->
                SearchResultRow(item)
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            }
        }
    }
}

@Composable
fun SearchResultRow(item: SearchResultItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = item.title, style = MaterialTheme.typography.titleMedium)
        Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen(
        state = SearchScreenState(
            query = "",
            searchResultList = listOf(
                SearchResultItem("Luke Skywalker", "Height: 172, Birth Year: 19BBY"),
                SearchResultItem("Darth Vader", "Height: 202, Birth Year: 41.9BBY")
            ),
            isLoading = false,
            onQueryChanged = {},
            onSearch = {}
        ),
    )
}

// --- State definitions ---

data class SearchScreenState(
    val query: String = "",
    val searchResultList: List<SearchResultItem> = emptyList(),
    val isLoading: Boolean = false,
    val onQueryChanged: (String) -> Unit = {},
    val onSearch: (String) -> Unit = {}
)

data class SearchResultItem(
    val title: String,
    val description: String,
)
