package com.cs407.seefood.data

import android.util.Log
import com.cs407.seefood.network.NutritionInfo
import com.cs407.seefood.network.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val TAG = "LlmRecipeClient"

/**
 * Client for talking to Groq's OpenAI-compatible /chat/completions endpoint.
 *
 * - suggest(): returns a List<Recipe> from the model or sample recipes.
 * - estimateNutrition(): returns NutritionInfo for ONE serving of a recipe.
 */
class LlmRecipeClient(
    private val groqApiKey: String
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val http: OkHttpClient by lazy {
        val log = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(log)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    // ---------- RECIPE SUGGESTIONS ----------

    suspend fun suggest(ingredients: List<String>): List<Recipe> = withContext(Dispatchers.IO) {
        if (groqApiKey.isBlank()) {
            Log.w(TAG, "Groq key blank → returning samples")
            return@withContext sampleRecipes()
        }

        val promptIng = ingredients.joinToString()
        Log.d(TAG, "suggest() with: $promptIng (Groq key length=${groqApiKey.length})")

        val bodyJson = """
            {
              "model": "llama-3.1-8b-instant",
              "temperature": 0.4,
              "messages": [
                {
                  "role": "system",
                  "content": "You are a cooking assistant. Reply with ONLY a valid JSON array of recipes. Do not wrap it in any object or explanation. Each recipe must have: id (string), title (string), imageUrl (string or null), ingredients (string array), steps (string array)."
                },
                {
                  "role": "user",
                  "content": "Ingredients: $promptIng. Create 3 approachable recipes that use many of these ingredients."
                }
              ]
            }
        """.trimIndent()

        val req = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .addHeader("Authorization", "Bearer $groqApiKey")
            .addHeader("Content-Type", "application/json")
            .post(bodyJson.toRequestBody(mediaType))
            .build()

        return@withContext runCatching {
            http.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    val msg = resp.body?.string().orEmpty()
                    Log.e(TAG, "Groq HTTP ${resp.code}: $msg")
                    throw IOException("HTTP ${resp.code}")
                }

                val txt = resp.body?.string().orEmpty()
                Log.d(TAG, "Raw Groq response snippet = ${txt.take(300)}")

                val root = json.parseToJsonElement(txt).jsonObject
                val content = root["choices"]!!
                    .jsonArray[0].jsonObject["message"]!!
                    .jsonObject["content"]!!.jsonPrimitive.content

                Log.d(TAG, "Groq content before cleaning = ${content.take(300)}")

                val cleaned = cleanJsonArray(content)
                Log.d(TAG, "Cleaned JSON candidate = ${cleaned.take(300)}")

                runCatching {
                    json.decodeFromString(
                        ListSerializer(Recipe.serializer()),
                        cleaned
                    )
                }.getOrElse { parseError ->
                    Log.w(
                        TAG,
                        "Groq JSON decode failed → using raw AI text as a single recipe",
                        parseError
                    )

                    listOf(
                        Recipe(
                            id = "groq-raw-1",
                            title = "Ideas using: $promptIng",
                            imageUrl = null,
                            ingredients = ingredients,
                            steps = cleaned
                                .split('\n')
                                .map { it.trim() }
                                .filter { it.isNotBlank() }
                        )
                    )
                }
            }
        }.getOrElse { e ->
            Log.e(TAG, "Groq suggest() failed → samples", e)
            sampleRecipes()
        }
    }

    /**
     * The model sometimes returns extra text or ```json fences.
     * This helper tries to pull out the JSON array portion.
     */
    private fun cleanJsonArray(raw: String): String {
        var s = raw.trim()

        if (s.startsWith("```")) {
            s = s.removePrefix("```json")
                .removePrefix("```JSON")
                .removePrefix("```")
        }
        if (s.endsWith("```")) {
            s = s.removeSuffix("```")
        }
        s = s.trim()

        if (s.startsWith("[")) return s

        val start = s.indexOf('[')
        val end = s.lastIndexOf(']')
        if (start != -1 && end != -1 && end > start) {
            return s.substring(start, end + 1)
        }
        return s
    }

    // ---------- NUTRITION ESTIMATION ----------

    suspend fun estimateNutrition(recipe: Recipe): NutritionInfo = withContext(Dispatchers.IO) {
        if (groqApiKey.isBlank()) {
            // heuristic fallback if key missing
            val ingredientCount = recipe.ingredients.size.coerceAtLeast(1)
            val calories = 250 + ingredientCount * 60
            val protein = (calories * 0.25 / 4).toInt()
            val carbs   = (calories * 0.45 / 4).toInt()
            val fat     = (calories * 0.30 / 9).toInt()
            return@withContext NutritionInfo(calories, protein, carbs, fat)
        }

        val ingredientText = recipe.ingredients.joinToString(", ")

        val bodyJson = """
            {
              "model": "llama-3.1-8b-instant",
              "temperature": 0.2,
              "messages": [
                {
                  "role": "system",
                  "content": "You are a nutrition assistant. Given a recipe, respond ONLY with a JSON object. Do not include any explanation. Format: {\"calories\": <int>, \"protein_g\": <int>, \"carbs_g\": <int>, \"fat_g\": <int>} for ONE serving."
                },
                {
                  "role": "user",
                  "content": "Recipe title: ${recipe.title}. Ingredients: $ingredientText. Estimate the nutrition for one typical serving."
                }
              ]
            }
        """.trimIndent()

        val req = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .addHeader("Authorization", "Bearer $groqApiKey")
            .addHeader("Content-Type", "application/json")
            .post(bodyJson.toRequestBody(mediaType))
            .build()

        runCatching {
            http.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    val msg = resp.body?.string().orEmpty()
                    Log.e(TAG, "Groq nutrition HTTP ${resp.code}: $msg")
                    throw IOException("HTTP ${resp.code}")
                }

                val txt = resp.body?.string().orEmpty()
                Log.d(TAG, "Nutrition raw response = ${txt.take(300)}")

                val root = json.parseToJsonElement(txt).jsonObject
                val content = root["choices"]!!
                    .jsonArray[0].jsonObject["message"]!!
                    .jsonObject["content"]!!.jsonPrimitive.content

                val cleaned = cleanJsonObject(content)
                Log.d(TAG, "Nutrition cleaned JSON = ${cleaned.take(300)}")

                json.decodeFromString(NutritionInfo.serializer(), cleaned)
            }
        }.getOrElse { e ->
            Log.e(TAG, "estimateNutrition() failed, falling back heuristic", e)
            val ingredientCount = recipe.ingredients.size.coerceAtLeast(1)
            val calories = 250 + ingredientCount * 60
            val protein = (calories * 0.25 / 4).toInt()
            val carbs   = (calories * 0.45 / 4).toInt()
            val fat     = (calories * 0.30 / 9).toInt()
            NutritionInfo(calories, protein, carbs, fat)
        }
    }

    /**
     * Pull out a JSON object from a reply that may be wrapped in
     * extra text or ``` fences.
     */
    private fun cleanJsonObject(raw: String): String {
        var s = raw.trim()

        if (s.startsWith("```")) {
            s = s.removePrefix("```json")
                .removePrefix("```JSON")
                .removePrefix("```")
        }
        if (s.endsWith("```")) {
            s = s.removeSuffix("```")
        }
        s = s.trim()

        val start = s.indexOf('{')
        val end = s.lastIndexOf('}')
        if (start != -1 && end != -1 && end > start) {
            return s.substring(start, end + 1)
        }
        return s
    }

    // ---------- FALLBACK SAMPLE RECIPES ----------

    private fun sampleRecipes(): List<Recipe> = listOf(
        Recipe(
            id = "sample-1",
            title = "Quick Veggie Wrap",
            imageUrl = null,
            ingredients = listOf("tortilla", "lettuce", "tomato", "cheese"),
            steps = listOf("Warm tortilla", "Layer veggies", "Roll and enjoy")
        ),
        Recipe(
            id = "sample-2",
            title = "Simple Pasta",
            imageUrl = null,
            ingredients = listOf("pasta", "olive oil", "garlic", "salt"),
            steps = listOf("Boil pasta", "Sauté garlic", "Toss with oil and pasta")
        ),
        Recipe(
            id = "sample-3",
            title = "Tomato Omelette",
            imageUrl = null,
            ingredients = listOf("eggs", "tomato", "onion", "salt"),
            steps = listOf("Beat eggs", "Add chopped veggies", "Cook in pan")
        )
    )
}
