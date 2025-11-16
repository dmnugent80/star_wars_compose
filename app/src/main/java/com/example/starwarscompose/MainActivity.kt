package com.example.starwarscompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.starwarscompose.feature.search.composables.SearchScreen
import com.example.starwarscompose.feature.search.viewModel.SearchViewModel
import com.example.starwarscompose.ui.theme.StarWarsComposeTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StarWarsComposeTheme {
                val viewModel: SearchViewModel = koinViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                SearchScreen(
                    state = state,
                    onIntent = viewModel::handleIntent
                )
            }
        }
    }
}
