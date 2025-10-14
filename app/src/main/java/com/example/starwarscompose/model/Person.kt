package com.example.starwarscompose.model

data class Person(
    val name: String,
    val height: String,
    val hairColor: String,
    val eyeColor: String,
    val birthYear: String,
    val films: List<String>
)