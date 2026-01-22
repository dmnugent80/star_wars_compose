package com.example.starwarscompose.fake

import com.example.starwarscompose.model.Film
import com.example.starwarscompose.model.Person

object TestData {
    val lukeSkywalker = Person(
        name = "Luke Skywalker",
        height = "172",
        hairColor = "blond",
        eyeColor = "blue",
        birthYear = "19BBY",
        films = listOf("https://swapi.tech/api/films/1")
    )

    val darthVader = Person(
        name = "Darth Vader",
        height = "202",
        hairColor = "none",
        eyeColor = "yellow",
        birthYear = "41.9BBY",
        films = listOf("https://swapi.tech/api/films/1")
    )

    val c3po = Person(
        name = "C-3PO",
        height = "167",
        hairColor = "n/a",
        eyeColor = "yellow",
        birthYear = "112BBY",
        films = listOf("https://swapi.tech/api/films/1", "https://swapi.tech/api/films/2")
    )

    val chewbacca = Person(
        name = "Chewbacca",
        height = "228",
        hairColor = "brown",
        eyeColor = "brown",
        birthYear = "200BBY",
        films = listOf("https://swapi.tech/api/films/1")
    )

    val aNewHope = Film(
        title = "A New Hope",
        director = "George Lucas",
        producer = "Gary Kurtz, Rick McCallum",
        releaseDate = "1977-05-25",
        openingCrawl = "It is a period of civil war...",
        url = "https://swapi.tech/api/films/1"
    )

    val empireStrikesBack = Film(
        title = "The Empire Strikes Back",
        director = "Irvin Kershner",
        producer = "Gary Kurtz, Rick McCallum",
        releaseDate = "1980-05-17",
        openingCrawl = "It is a dark time for the Rebellion...",
        url = "https://swapi.tech/api/films/2"
    )
}
