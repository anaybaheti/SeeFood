//// app/src/main/java/com/cs407/seefood/data/SeeFoodRepository.kt
//package com.cs407.seefood.data
//
//import com.cs407.seefood.BuildConfig
//import com.cs407.seefood.network.Recipe
//
//class SeeFoodRepository {
//    private val apiKey: String = BuildConfig.OPENAI_API_KEY
//    private val llm = LlmRecipeClient(apiKey)
//
//    suspend fun suggestRecipesFrom(ingredients: List<String>): List<Recipe> {
//        return llm.suggest(ingredients)
//    }
//}

package com.cs407.seefood.data

import com.cs407.seefood.BuildConfig
import com.cs407.seefood.network.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SeeFoodRepository {
    private val apiKey: String = BuildConfig.OPENAI_API_KEY
    private val llm = LlmRecipeClient(openAiApiKey = apiKey)

    suspend fun suggestRecipesFrom(ingredients: List<String>): List<Recipe> =
        withContext(Dispatchers.IO) {
            llm.suggest(ingredients)
        }
}

