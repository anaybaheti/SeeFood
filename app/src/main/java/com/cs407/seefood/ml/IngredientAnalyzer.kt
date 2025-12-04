package com.cs407.seefood.ml

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

/**
 * IngredientAnalyzer
 *
 * - Uses ML Kit's on-device image labeling.
 * - Filters out obvious non-food / environment labels.
 * - Keeps the top few "food-ish" labels and sends them to the UI.
 * - User can still delete bad ones or add missing ones manually.
 */
class IngredientAnalyzer(
    private val onIngredients: (List<String>) -> Unit
) : ImageAnalysis.Analyzer {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.30f) // keep this around 0.25–0.35
            .build()
    )

    private var lastEmitted = emptyList<String>()
    private var frameCounter = 0

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val rotation = imageProxy.imageInfo.rotationDegrees
        val input = InputImage.fromMediaImage(mediaImage, rotation)

        labeler.process(input)
            .addOnSuccessListener { labels ->
                if (labels.isEmpty()) {
                    imageProxy.close()
                    return@addOnSuccessListener
                }

                // Debug log so you can see what ML Kit is outputting
                labels.forEach {
                    Log.d("IngredientAnalyzer", "raw label=${it.text}, conf=${it.confidence}")
                }

                val normalized = labels
                    .sortedByDescending { it.confidence }
                    .map { it.text.lowercase().replace("_", " ").trim() }
                    .map { normalizeLabel(it) }
                    .filter { it.isNotBlank() }

                // Remove obvious non-food context words
                val foods = normalized
                    .filter { looksLikeFood(it) }
                    .distinct()
                    .take(6)

                if (foods.isEmpty()) {
                    // no food-ish labels this frame → keep last list
                    return@addOnSuccessListener
                }

                frameCounter++
                val changed =
                    foods.size != lastEmitted.size || foods.any { it !in lastEmitted }

                if (changed || frameCounter % 5 == 0) {
                    lastEmitted = foods
                    onIngredients(foods)
                }
            }
            .addOnFailureListener { e ->
                Log.e("IngredientAnalyzer", "ML error", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    companion object {

        // Clearly non-food environmental words; we drop any label that contains one of these
        private val NON_FOOD_BLACKLIST = setOf(
            "room","tile","wall","floor","ceiling","space",
            "shelf","furniture","chair","table","sofa","window",
            "building","lamp","picture","frame","door","cabinet"
        )

        // Words that usually refer to food / dishes (even if not specific ingredients)
//        private val FOOD_CONTEXT = setOf(
//            "food","dish","meal","snack","breakfast","lunch","dinner",
//            "plate","bowl","cup","mug","bottle","glass","sauce","salad","soup"
//        )

        // Common ingredient tokens (helps us normalize multi-word labels)
        private val FOOD_TOKENS = setOf(
            "apple","banana","orange","grape","berry","strawberry","blueberry",
            "lemon","lime","mango","pineapple",
            "tomato","potato","carrot","onion","garlic","pepper","broccoli","lettuce",
            "spinach","cabbage","cucumber","zucchini","corn","mushroom","pepperoni",
            "bread","toast","bagel","bun","sandwich","pizza","pasta","spaghetti",
            "noodle","rice","tortilla","wrap","burger","cereal","oatmeal",
            "egg","omelette","omelet","cheese","butter","yogurt","milk","cream",
            "chicken","beef","pork","steak","fish","salmon","shrimp","meat","tofu",
            "coffee","tea","juice","soda","cola","smoothie","water","milkshake",
            "chocolate","cookie","cake","donut","biscuit","candy","snack","chip",
            "cracker","brownie","ice cream","dessert"
        )

        private fun normalizeLabel(label: String): String {
            var s = label

            // If the label contains a known food token (e.g. "slice of cheese"), keep that token
            val token = FOOD_TOKENS.firstOrNull { s.contains(it) }
            if (token != null) return token

            // Simple plural handling
            s = when {
                s.endsWith("ies") && s.length > 3 -> s.dropLast(3) + "y" // berries -> berry
                s.endsWith("es") && s.length > 3 -> s.dropLast(2)       // tomatoes -> tomato
                s.endsWith("s")  && s.length > 3 -> s.dropLast(1)       // eggs -> egg
                else -> s
            }

            return s
        }

        fun looksLikeFood(label: String): Boolean {
            val l = label.lowercase()

            // Reject obvious environment
            if (NON_FOOD_BLACKLIST.any { l.contains(it) }) return false

            // If it contains an ingredient token, it's food
            if (FOOD_TOKENS.any { l.contains(it) }) return true

            // If it contains a generic "food context" word, treat as food-related
//            if (FOOD_CONTEXT.any { l.contains(it) }) return true

            return false
        }
    }
}


