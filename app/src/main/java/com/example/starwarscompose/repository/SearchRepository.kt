package com.example.starwarscompose.repository

interface SearchRepository {
    fun getSearchResults(query: String): List<String>
}
