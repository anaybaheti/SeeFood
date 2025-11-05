//package com.cs407.seefood.ui
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.cs407.seefood.data.SeeFoodRepository
//import com.cs407.seefood.network.Recipe
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class SeeFoodViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val repo = SeeFoodRepository(application.applicationContext)
//
//    // All detected/entered ingredients
//    private val _ingredients = MutableStateFlow<List<String>>(emptyList())
//    val ingredients: StateFlow<List<String>> = _ingredients
//
//    // User-selected subset (checkboxes)
//    private val _selected = MutableStateFlow<Set<String>>(emptySet())
//    val selected: StateFlow<Set<String>> = _selected
//
//    // Scanning state for ScanScreen
//    private val _scanning = MutableStateFlow(true)
//    val scanning: StateFlow<Boolean> = _scanning
//
//    // --- LLM results exposed as the UI needs them ---
//    // Current suggestions (list of Recipe)
//    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
//    val recipes: StateFlow<List<Recipe>> = _recipes
//
//    // Past suggestions: each entry is a list of Recipe that was shown previously
//    private val _history = MutableStateFlow<List<List<Recipe>>>(emptyList())
//    val history: StateFlow<List<List<Recipe>>> = _history
//
//    // Loading flag
//    private val _loading = MutableStateFlow(false)
//    val loading: StateFlow<Boolean> = _loading
//
//    // ----- actions -----
//
//    fun setScanning(on: Boolean) { _scanning.value = on }
//
//    /** Called by CameraPreview when new items are detected */
//    fun onScanUpdate(newOnes: List<String>) {
//        if (newOnes.isEmpty()) return
//        val merged = (_ingredients.value + newOnes)
//            .map { it.trim() }
//            .filter { it.isNotEmpty() }
//            .distinct()
//        _ingredients.value = merged
//        // default to all selected
//        _selected.value = merged.toSet()
//    }
//
//    fun toggleIngredient(name: String) {
//        _selected.value = _selected.value.let { set ->
//            if (set.contains(name)) set - name else set + name
//        }
//    }
//
//    fun addManual(name: String) {
//        val n = name.trim()
//        if (n.isEmpty()) return
//        val merged = (_ingredients.value + n).distinct()
//        _ingredients.value = merged
//        _selected.value = _selected.value + n
//    }
//
//    fun setIngredients(list: List<String>) {
//        val clean = list.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
//        _ingredients.value = clean
//        _selected.value = clean.toSet()
//    }
//
//    fun generateRecipes() {
//        val use = _selected.value.ifEmpty { _ingredients.value.toSet() }.toList()
//        if (use.isEmpty()) return
//        viewModelScope.launch {
//            _loading.value = true
//            try {
//                val resp = repo.suggestRecipesFrom(use)   // returns RecipeIdeasResponse
//                val current = resp.recipes               // convert to List<Recipe>
//                _recipes.value = current
//                _history.value = listOf(current) + _history.value
//            } finally {
//                _loading.value = false
//            }
//        }
//    }
//}
//


package com.cs407.seefood.ui

import androidx.lifecycle.viewModelScope
import com.cs407.seefood.network.Recipe
import kotlinx.coroutines.launch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.cs407.seefood.data.SeeFoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SeeFoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SeeFoodRepository(application.applicationContext)

    // All detected/entered ingredients
    private val _ingredients = MutableStateFlow<List<String>>(emptyList())
    val ingredients: StateFlow<List<String>> = _ingredients

    // User-selected subset (checkboxes)
    private val _selected = MutableStateFlow<Set<String>>(emptySet())
    val selected: StateFlow<Set<String>> = _selected

    // Scanning state for ScanScreen
    private val _scanning = MutableStateFlow(true)
    val scanning: StateFlow<Boolean> = _scanning

    // --- UI-facing state (aligns with SeeFoodApp.kt) ---
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _history = MutableStateFlow<List<List<Recipe>>>(emptyList())
    val history: StateFlow<List<List<Recipe>>> = _history

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // ----- keep your other helpers (setScanning, onScanUpdate, toggleIngredient, addManual, setIngredients) -----

    fun generateRecipes() {
        // Avoid ifEmpty{} to dodge stdlib/version issues
        val chosen = _selected.value.toList()
        val use: List<String> = if (chosen.isNotEmpty()) chosen else _ingredients.value

        if (use.isEmpty()) return

        viewModelScope.launch {
            _loading.value = true
            try {
                val resp = repo.suggestRecipesFrom(use)  // returns RecipeIdeasResponse
                val current: List<Recipe> = resp.recipes
                _recipes.value = current
                _history.value = listOf(current) + _history.value
            } finally {
                _loading.value = false
            }
        }
    }
}
