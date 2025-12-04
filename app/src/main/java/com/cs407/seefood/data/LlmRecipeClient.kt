package com.cs407.seefood.data

import android.util.Log
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
 * - If the call succeeds and JSON parses: returns a List<Recipe> from the model.
 * - If JSON parsing fails: returns a single Recipe built from the raw AI text.
 * - If HTTP fails (429, network, etc.) or key is blank: returns sampleRecipes().
 */
class LlmRecipeClient(
    private val groqApiKey: String   // we still read it from BuildConfig.OPENAI_API_KEY
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

    suspend fun suggest(ingredients: List<String>): List<Recipe> = withContext(Dispatchers.IO) {
        if (groqApiKey.isBlank()) {
            Log.w(TAG, "Groq key blank → returning samples")
            return@withContext sampleRecipes()
        }

        val promptIng = ingredients.joinToString()
        Log.d(TAG, "suggest() with: $promptIng (Groq key length=${groqApiKey.length})")

        // Groq's OpenAI-compatible chat completions endpoint
        // Docs: https://console.groq.com/docs
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

                // Groq uses the same shape as OpenAI:
                // { choices:[{message:{content:"<TEXT>"}}, ...] }
                val root = json.parseToJsonElement(txt).jsonObject
                val content = root["choices"]!!
                    .jsonArray[0].jsonObject["message"]!!
                    .jsonObject["content"]!!.jsonPrimitive.content

                Log.d(TAG, "Groq content before cleaning = ${content.take(300)}")

                val cleaned = cleanJsonArray(content)
                Log.d(TAG, "Cleaned JSON candidate = ${cleaned.take(300)}")

                // 1) Primary path: try to decode as JSON array of Recipe
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

                    // 2) Secondary path: still return something AI-based
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

        // Strip markdown-style ```json fences if present
        if (s.startsWith("```")) {
            s = s.removePrefix("```json")
                .removePrefix("```JSON")
                .removePrefix("```")
        }
        if (s.endsWith("```")) {
            s = s.removeSuffix("```")
        }
        s = s.trim()

        // If it already looks like an array, return as-is
        if (s.startsWith("[")) return s

        // Otherwise, try to extract from the first '[' to the last ']'
        val start = s.indexOf('[')
        val end = s.lastIndexOf(']')
        if (start != -1 && end != -1 && end > start) {
            return s.substring(start, end + 1)
        }

        // As a last resort, just return s; caller will then use "raw text" fallback
        return s
    }

    // Only used if HTTP/key fail entirely.
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
