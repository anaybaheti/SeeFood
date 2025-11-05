package com.cs407.seefood.data

import com.cs407.seefood.network.IngredientsResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class LlmRecipeClient(
    private val provider: Provider,
    private val apiKey: String
) {
    enum class Provider { OPENAI, GEMINI }

    private val http = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun suggest(ingredients: List<String>): IngredientsResponse {
        val prompt = buildPrompt(ingredients)
        return when (provider) {
            Provider.OPENAI -> callOpenAi(prompt)
            Provider.GEMINI -> callGemini(prompt)
        }
    }

    private fun buildPrompt(ings: List<String>) = """
        You are a helpful cooking assistant. Given a set of ingredients, propose 3 simple recipes.
        Constraints:
        - Use only common pantry items + the provided ingredients when possible.
        - Output STRICT JSON with this schema (no markdown, no extra text):
          {
            "recipes": [
              {"title": "...", "ingredients": ["..."], "steps": ["..."]},
              {"title": "...", "ingredients": ["..."], "steps": ["..."]},
              {"title": "...", "ingredients": ["..."], "steps": ["..."]}
            ]
          }
        Ingredients: ${ings.joinToString(", ")}
    """.trimIndent()

    private fun callOpenAi(prompt: String): IngredientsResponse {
        val body = """
            {"model":"gpt-4o-mini",
             "messages":[{"role":"user","content": ${prompt.encodeJsonString()}}],
             "temperature":0.6}
        """.trimIndent()

        val req = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        http.newCall(req).execute().use { resp ->
            val text = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) error("OpenAI HTTP ${resp.code}: $text")

            val obj = json.parseToJsonElement(text).jsonObject
            val content = obj["choices"]!!.jsonArray[0]
                .jsonObject["message"]!!.jsonObject["content"]!!.jsonPrimitive.content
                .stripCodeFences()

            return json.decodeFromString(IngredientsResponse.serializer(), content)
        }
    }

    private fun callGemini(prompt: String): IngredientsResponse {
        val body = """{ "contents":[{ "parts":[{ "text": ${prompt.encodeJsonString()} }]}] }"""

        val req = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        http.newCall(req).execute().use { resp ->
            val text = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) error("Gemini HTTP ${resp.code}: $text")

            val obj = json.parseToJsonElement(text).jsonObject
            val content = obj["candidates"]!!.jsonArray[0]
                .jsonObject["content"]!!.jsonObject["parts"]!!.jsonArray[0]
                .jsonObject["text"]!!.jsonPrimitive.content
                .stripCodeFences()

            return json.decodeFromString(IngredientsResponse.serializer(), content)
        }
    }
}

// helpers
private fun String.encodeJsonString(): String = buildString {
    append('"')
    for (ch in this@encodeJsonString) when (ch) {
        '\\' -> append("\\\\"); '"' -> append("\\\"")
        '\n' -> append("\\n"); '\r' -> append("\\r"); '\t' -> append("\\t")
        else -> append(ch)
    }
    append('"')
}

private fun String.stripCodeFences(): String {
    var s = trim()
    if (s.startsWith("```")) s = s.removePrefix("```json").removePrefix("```").trim()
    if (s.endsWith("```")) s = s.removeSuffix("```").trim()
    return s
}
