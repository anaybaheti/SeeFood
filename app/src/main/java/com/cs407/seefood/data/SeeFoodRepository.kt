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

import android.util.Log
import com.cs407.seefood.BuildConfig
import com.cs407.seefood.network.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SeeFoodRepository {

    // This still reads from BuildConfig.OPENAI_API_KEY,
    // but now the value is actually your Groq key (gsk_...)
    private val apiKey: String = BuildConfig.OPENAI_API_KEY

    // Pass it into LlmRecipeClient using the correct parameter name
    private val llm = LlmRecipeClient(groqApiKey = apiKey)

    init {
        Log.d("SeeFoodRepository", "Groq/OpenAI key length = ${apiKey.length}")
    }

    suspend fun suggestRecipesFrom(ingredients: List<String>): List<Recipe> =
        withContext(Dispatchers.IO) {
            llm.suggest(ingredients)
        }
}


