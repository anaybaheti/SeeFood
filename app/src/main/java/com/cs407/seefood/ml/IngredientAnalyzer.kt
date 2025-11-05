package com.cs407.seefood.ml

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

/**
 * Converts camera frames to ingredient names using ML Kit Image Labeling.
 * Emits debounced results via onIngredients callback.
 */
class IngredientAnalyzer(
    private val onIngredients: (List<String>) -> Unit
) : ImageAnalysis.Analyzer {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.6f) // tune as needed
            .build()
    )

    // simple debouncer so we don't spam UI every frame
    private var lastEmitted = emptyList<String>()
    private var frameCounter = 0

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close(); return
        }
        val rotation = imageProxy.imageInfo.rotationDegrees
        val input = InputImage.fromMediaImage(mediaImage, rotation)

        labeler.process(input)
            .addOnSuccessListener { labels ->
                // Map generic labels -> ingredient-like tokens
                val raw = labels.map { it.text.lowercase() }

                // Basic normalization: keep food-ish words; strip plurals; dedupe
                val keep = raw
                    .map { it.replace("_", " ") }
                    .map { if (it.endsWith("s")) it.dropLast(1) else it }
                    .filter { FOOD_HINTS.any { hint -> it.contains(hint) } || FOOD_WHITELIST.contains(it) }
                    .distinct()

                // Emit every ~5 frames or when there’s a change
                frameCounter++
                val changed = keep.size != lastEmitted.size || keep.any { it !in lastEmitted }
                if ((frameCounter % 5 == 0) || changed) {
                    lastEmitted = keep
                    onIngredients(keep)
                }
            }
            .addOnFailureListener { e -> Log.e("IngredientAnalyzer", "ML error", e) }
            .addOnCompleteListener { imageProxy.close() }
    }

    companion object {
        // Cheap heuristics to filter to likely foods
        private val FOOD_HINTS = listOf("apple","banana","tomato","potato","carrot","onion","bread","milk","egg","cheese",
            "lettuce","spinach","cucumber","pepper","pasta","noodle","rice","meat","chicken","beef","fish","yogurt","butter")
        private val FOOD_WHITELIST = FOOD_HINTS.toSet()
    }
}
