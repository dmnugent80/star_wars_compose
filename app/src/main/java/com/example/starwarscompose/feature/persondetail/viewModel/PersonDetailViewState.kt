package com.example.starwarscompose.feature.persondetail.viewModel

import com.example.starwarscompose.feature.search.composables.AnimationType

data class PersonDetailViewState(
    val name: String = "",
    val height: String = "",
    val hairColor: String = "",
    val eyeColor: String = "",
    val birthYear: String = "",
    val filmUrls: List<String> = emptyList(),
    val animationType: AnimationType = AnimationType.FADE_GRAY
)
