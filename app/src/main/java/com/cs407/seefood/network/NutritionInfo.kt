package com.cs407.seefood.network

import kotlinx.serialization.Serializable

@Serializable
data class NutritionInfo(
    val calories: Int,
    val protein_g: Int,
    val carbs_g: Int,
    val fat_g: Int
)
