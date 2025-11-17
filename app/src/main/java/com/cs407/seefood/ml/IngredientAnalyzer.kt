//package com.cs407.seefood.ml
//
//import android.annotation.SuppressLint
//import android.util.Log
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.ImageProxy
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.label.ImageLabeling
//import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
//
///**
// * Converts camera frames to ingredient names using ML Kit Image Labeling.
// * Emits debounced results via onIngredients callback.
// */
//class IngredientAnalyzer(
//    private val onIngredients: (List<String>) -> Unit
//) : ImageAnalysis.Analyzer {
//
//    private val labeler = ImageLabeling.getClient(
//        ImageLabelerOptions.Builder()
//            .setConfidenceThreshold(0.30f) // tune as needed
//            .build()
//    )
//
//    // simple debouncer so we don't spam UI every frame
//    private var lastEmitted = emptyList<String>()
//    private var frameCounter = 0
//
//    @SuppressLint("UnsafeOptInUsageError")
//    override fun analyze(imageProxy: ImageProxy) {
//        val mediaImage = imageProxy.image
//        if (mediaImage == null) {
//            imageProxy.close(); return
//        }
//        val rotation = imageProxy.imageInfo.rotationDegrees
//        val input = InputImage.fromMediaImage(mediaImage, rotation)
//
//        labeler.process(input)
//            .addOnSuccessListener { labels ->
//                // Map generic labels -> ingredient-like tokens
//                val raw = labels.map { it.text.lowercase() }
//
//                // Basic normalization: keep food-ish words; strip plurals; dedupe
//                val keep = raw
//                    .map { it.replace("_", " ") }
//                    .map { if (it.endsWith("s")) it.dropLast(1) else it }
//                    .filter { FOOD_HINTS.any { hint -> it.contains(hint) } || FOOD_WHITELIST.contains(it) }
//                    .distinct()
//
//                // Emit every ~5 frames or when there’s a change
//                frameCounter++
//                val changed = keep.size != lastEmitted.size || keep.any { it !in lastEmitted }
//                if ((frameCounter % 5 == 0) || changed) {
//                    lastEmitted = keep
//                    onIngredients(keep)
//                }
//            }
//            .addOnFailureListener { e -> Log.e("IngredientAnalyzer", "ML error", e) }
//            .addOnCompleteListener { imageProxy.close() }
//    }
//
//    companion object {
//        // Cheap heuristics to filter to likely foods
//        private val FOOD_HINTS = listOf("soda","chocolate bar","apple","banana","tomato","potato","carrot","onion","bread","milk","egg","cheese",
//            "lettuce","spinach","cucumber","pepper","pasta","noodle","rice","meat","chicken","beef","fish","yogurt","butter", "ketchup","coffee", "drink")
//        private val FOOD_WHITELIST = FOOD_HINTS.toSet()
//    }
//}

package com.cs407.seefood.ml

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

/**
 * Converts camera frames to ingredient-like labels using ML Kit Image Labeling.
 * Emits debounced results via onIngredients callback.
 *
 * This version:
 *  - lets ML Kit generate labels for anything
 *  - THEN filters down to only "food-ish" labels using a whitelist
 */
class IngredientAnalyzer(
    private val onIngredients: (List<String>) -> Unit
) : ImageAnalysis.Analyzer {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.30f)
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
                // All raw labels from ML Kit, normalized
                val raw = labels
                    .sortedByDescending { it.confidence }
                    .map { it.text.lowercase().replace("_", " ") }
                    .map { if (it.endsWith("s")) it.dropLast(1) else it }

                // Keep ONLY labels that look like food ingredients/items
                val foods = raw
                    .filter { looksLikeFood(it) }
                    .distinct()
                    .take(8) // no need to spam with a huge list

                if (foods.isEmpty()) {
                    // No food detected in this frame → do nothing, keep previous list
                    return@addOnSuccessListener
                }

                frameCounter++
                val changed =
                    foods.size != lastEmitted.size || foods.any { it !in lastEmitted }

                if ((frameCounter % 5 == 0) || changed) {
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
        // Very lightweight "is this food?" heuristic
        private val FOOD_KEYWORDS = setOf(
            "apple","banana","orange","grape","berry","strawberry","blueberry",
            "tomato","potato","carrot","onion","garlic","pepper","broccoli","lettuce",
            "spinach","cabbage","cucumber","zucchini","corn","mushroom",

            "bread","toast","bagel","bun","sandwich","pizza","pasta","spaghetti",
            "noodle","rice","tortilla","wrap","bun","burger",

            "egg","omelette","cheese","butter","yogurt","milk","cream",

            "chicken","beef","pork","steak","fish","salmon","shrimp","meat",

            "coffee","tea","juice","soda","drink","smoothie","water bottle",

            "chocolate","cookie","cake","donut","biscuit","candy","snack","cereal"
        )

        fun looksLikeFood(label: String): Boolean {
            // Exact or substring match against keyword list
            if (label in FOOD_KEYWORDS) return true
            return FOOD_KEYWORDS.any { kw -> label.contains(kw) }
        }
    }
}
