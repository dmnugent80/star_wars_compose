package com.example.starwarscompose.repository

class SearchRepositoryImpl : SearchRepository {
    override fun getSearchResults(query: String): List<String> {
        val allData = listOf("Apple", "Banana", "Orange", "Grapes", "Mango")
        return allData.filter { it.contains(query, ignoreCase = true) }
    }
}
