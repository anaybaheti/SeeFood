package com.cs407.seefood.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("ingredients")
    suspend fun getIngredients(): IngredientsResponse

    @GET("recipes")
    suspend fun getRecipes(
        @Query("ingredients") csv: String
    ): List<Recipe>
}
