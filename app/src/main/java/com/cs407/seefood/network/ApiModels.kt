package com.cs407.seefood.network

import kotlinx.serialization.Serializable

@Serializable
data class IngredientsResponse(
    val ingredients: List<String>
)

@Serializable
data class Recipe(
    val id: String,
    val title: String,
    val imageUrl: String? = null,
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList()
)
