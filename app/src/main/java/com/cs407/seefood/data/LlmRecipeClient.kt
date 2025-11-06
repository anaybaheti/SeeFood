//
//
//
//package com.cs407.seefood.data
//
//import com.cs407.seefood.network.Recipe
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.json.JsonArray
//import kotlinx.serialization.json.jsonArray
//import kotlinx.serialization.json.jsonObject
//import kotlinx.serialization.json.jsonPrimitive
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import java.util.concurrent.TimeUnit
//
///**
// * Calls OpenAI Chat Completions and parses a JSON list of Recipe.
// * If key is blank OR any network/parse error occurs, we return local sample recipes.
// */
//class LlmRecipeClient(
//    private val openAiApiKey: String
//) {
//    private val http = OkHttpClient.Builder()
//        .connectTimeout(10, TimeUnit.SECONDS)
//        .readTimeout(20, TimeUnit.SECONDS)
//        .build()
//
//    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }
//
//    suspend fun suggest(ingredients: List<String>): List<Recipe> {
//        // 1) Fallback immediately if key is empty
//        if (openAiApiKey.isBlank()) return sampleRecipes()
//
//        return try {
//            val system =
//                """You are a cooking assistant.
//                   Return ONLY valid JSON: an array of recipes.
//                   Each recipe must have: id (string), title (string), imageUrl (string or null),
//                   ingredients (string list), steps (string list).""".trimIndent()
//
//            val user =
//                """Ingredients: ${ingredients.joinToString(", ")}
//                   Create 3 approachable recipes that use many of these ingredients.""".trimIndent()
//
//            val body = """
//                {
//                  "model": "gpt-4o-mini",
//                  "temperature": 0.5,
//                  "messages": [
//                    {"role":"system","content": ${system.encodeJsonString()}},
//                    {"role":"user","content": ${user.encodeJsonString()}}
//                  ],
//                  "response_format": {"type":"json_object"}
//                }
//            """.trimIndent()
//
//            val req = Request.Builder()
//                .url("https://api.openai.com/v1/chat/completions")
//                .addHeader("Authorization", "Bearer $openAiApiKey")
//                .addHeader("Content-Type", "application/json")
//                .post(body.toRequestBody("application/json".toMediaType()))
//                .build()
//
//            http.newCall(req).execute().use { resp ->
//                val text = resp.body?.string().orEmpty()
//                if (!resp.isSuccessful) throw IllegalStateException("OpenAI HTTP ${resp.code}: $text")
//
//                val root = json.parseToJsonElement(text).jsonObject
//                val choices = root["choices"]!!.jsonArray
//                val message = choices[0].jsonObject["message"]!!.jsonObject
//                val content = message["content"]!!.jsonPrimitive.content
//
//                // Accept { "recipes":[...] } OR just [...]
//                return try {
//                    val obj = json.parseToJsonElement(content).jsonObject
//                    val arr = (obj["recipes"] as? JsonArray) ?: obj.values.firstOrNull() as? JsonArray
//                    json.decodeFromJsonElement(ListSerializerRecipe, arr ?: JsonArray(emptyList()))
//                } catch (_: Throwable) {
//                    val arr = json.parseToJsonElement(content).jsonArray
//                    json.decodeFromJsonElement(ListSerializerRecipe, arr)
//                }
//            }
//        } catch (t: Throwable) {
//            // 2) ANY error → return samples so UI still works
//            sampleRecipes()
//        }
//    }
//
//    private fun sampleRecipes(): List<Recipe> = listOf(
//        Recipe(
//            id = "sample-1",
//            title = "Quick Veggie Wrap",
//            ingredients = listOf("tortilla", "lettuce", "tomato", "cheese"),
//            steps = listOf("Warm tortilla", "Layer veggies", "Roll and enjoy")
//        ),
//        Recipe(
//            id = "sample-2",
//            title = "Simple Pasta",
//            ingredients = listOf("pasta", "olive oil", "garlic", "salt"),
//            steps = listOf("Boil pasta", "Sauté garlic", "Toss with oil and pasta")
//        ),
//        Recipe(
//            id = "sample-3",
//            title = "Tomato Omelette",
//            ingredients = listOf("eggs", "tomato", "onion", "salt"),
//            steps = listOf("Beat eggs", "Sauté veggies", "Cook omelette")
//        )
//    )
//
//    // helpers
//    private fun String.encodeJsonString(): String = buildString {
//        append('"')
//        for (c in this@encodeJsonString) {
//            when (c) {
//                '\\' -> append("\\\\")
//                '"' -> append("\\\"")
//                '\n' -> append("\\n")
//                '\r' -> append("\\r")
//                '\t' -> append("\\t")
//                else -> append(c)
//            }
//        }
//        append('"')
//    }
//
//    companion object {
//        private val ListSerializerRecipe =
//            kotlinx.serialization.builtins.ListSerializer(Recipe.serializer())
//    }
//}

package com.cs407.seefood.data

import android.util.Log
import com.cs407.seefood.network.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
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

class LlmRecipeClient(
    private val openAiApiKey: String
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

    /**
     * Always runs on Dispatchers.IO.
     * Never throws to callers. Returns sample recipes on any error.
     */
    suspend fun suggest(ingredients: List<String>): List<Recipe> = withContext(Dispatchers.IO) {
        if (openAiApiKey.isBlank()) {
            Log.w(TAG, "OPENAI key blank → returning samples")
            return@withContext sampleRecipes()
        }

        val promptIng = ingredients.joinToString()
        Log.d(TAG, "suggest() with: $promptIng")

        val bodyJson = """
            {
              "model": "gpt-4o-mini",
              "temperature": 0.4,
              "response_format": { "type": "json_object" },
              "messages": [
                {
                  "role": "system",
                  "content": "You are a cooking assistant. Return ONLY valid JSON array of recipes. Each recipe: id (string), title (string), imageUrl (string or null), ingredients (string[]), steps (string[])."
                },
                {
                  "role": "user",
                  "content": "Ingredients: $promptIng. Create 3 approachable recipes that use many of these ingredients."
                }
              ]
            }
        """.trimIndent()

        val req = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $openAiApiKey")
            .addHeader("Content-Type", "application/json")
            .post(bodyJson.toRequestBody(mediaType))
            .build()

        return@withContext runCatching {
            http.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    val msg = resp.body?.string().orEmpty()
                    Log.e(TAG, "OpenAI HTTP ${resp.code}: $msg")
                    throw IOException("HTTP ${resp.code}")
                }
                val txt = resp.body?.string().orEmpty()
                // openai returns { choices:[{message:{content:"<JSON>"}}, ...] }
                val root = json.parseToJsonElement(txt).jsonObject
                val content = root["choices"]!!
                    .jsonArray[0].jsonObject["message"]!!
                    .jsonObject["content"]!!.jsonPrimitive.content

                json.decodeFromString(ListSerializer(Recipe.serializer()), content)
            }
        }.getOrElse { e ->
            Log.e(TAG, "suggest() failed → samples", e)
            sampleRecipes()
        }
    }

    // Your existing sample data (keep or edit)
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
