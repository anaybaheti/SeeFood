package com.cs407.seefood.data

import android.content.Context
import com.cs407.seefood.R
import com.cs407.seefood.data.LlmRecipeClient.Provider
import com.cs407.seefood.network.IngredientsResponse

class SeeFoodRepository(private val context: Context) {

    private val apiKey: String by lazy { context.getString(R.string.openai_api_key) }

    private val llm = LlmRecipeClient(Provider.OPENAI, apiKey)

    suspend fun suggestRecipesFrom(ingredients: List<String>): IngredientsResponse {
        require(apiKey.isNotBlank()) { "Missing OPENAI_API_KEY (gradle.properties -> resValue)" }
        return llm.suggest(ingredients)
    }
}
