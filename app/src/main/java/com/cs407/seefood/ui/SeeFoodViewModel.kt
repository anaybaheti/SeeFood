package com.cs407.seefood.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.seefood.data.SeeFoodRepository
import com.cs407.seefood.network.Recipe
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SeeFoodViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = SeeFoodRepository()

    // ---------- FIRESTORE (points to database ID "seefood1") ----------
    private val db: FirebaseFirestore =
        FirebaseFirestore.getInstance(FirebaseApp.getInstance(), "seefood1")

    // ---------- USER PROFILE STATE ----------

    var firstName by mutableStateOf<String?>(null)
        private set

    var lastName by mutableStateOf<String?>(null)
        private set

    var email by mutableStateOf<String?>(null)
        private set

    fun setUserProfile(first: String, last: String, mail: String) {
        firstName = first
        lastName = last
        email = mail
    }

    fun clearUserProfile() {
        firstName = null
        lastName = null
        email = null
    }

    /**
     * Save the user's profile into Firestore under:
     *   users/{uid}
     *
     * Call this right after a successful SIGN-UP.
     */
    fun saveUserProfileToFirestore(
        uid: String,
        first: String,
        last: String,
        mail: String,
        onResult: (Boolean) -> Unit
    ) {
        val userMap = hashMapOf(
            "firstName" to first,
            "lastName" to last,
            "email" to mail
        )

        db.collection("users")
            .document(uid)
            .set(userMap)
            .addOnSuccessListener {
                // also update local state
                setUserProfile(first, last, mail)
                onResult(true)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onResult(false)
            }
    }

    /**
     * Load the user's profile from Firestore:
     *   users/{uid}
     *
     * Call this right after a successful LOGIN.
     */
    fun loadUserProfileFromFirestore(
        uid: String,
        onResult: (Boolean) -> Unit
    ) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { snap ->
                if (snap != null && snap.exists()) {
                    val first = snap.getString("firstName") ?: ""
                    val last = snap.getString("lastName") ?: ""
                    val mail = snap.getString("email") ?: ""

                    setUserProfile(first, last, mail)
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onResult(false)
            }
    }

    // ---------- INGREDIENTS / RECIPES STATE ----------

    private val _ingredients = MutableStateFlow(emptyList<String>())
    val ingredients: StateFlow<List<String>> = _ingredients

    private val _selected = MutableStateFlow(emptySet<String>())
    val selected: StateFlow<Set<String>> = _selected

    private val _scanning = MutableStateFlow(true)
    val scanning: StateFlow<Boolean> = _scanning

    // current recipes
    private val _current = MutableStateFlow(emptyList<Recipe>())
    val current: StateFlow<List<Recipe>> = _current

    // history of recipe batches
    private val _history = MutableStateFlow(emptyList<List<Recipe>>())
    val history: StateFlow<List<List<Recipe>>> = _history

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // ---------- ACTIONS ----------

    fun setScanning(on: Boolean) {
        _scanning.value = on
    }

    fun onScanUpdate(newOnes: List<String>) {
        if (newOnes.isEmpty()) return
        val merged = (_ingredients.value + newOnes)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
        _ingredients.value = merged
        _selected.value = merged.toSet()
    }

    fun toggleIngredient(name: String) {
        _selected.value = _selected.value.let { s ->
            if (name in s) s - name else s + name
        }
    }

    fun addManual(name: String) {
        val n = name.trim()
        if (n.isEmpty()) return
        val merged = (_ingredients.value + n).distinct()
        _ingredients.value = merged
        _selected.value = _selected.value + n
    }

    fun setIngredients(list: List<String>) {
        val clean = list
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
        _ingredients.value = clean
        _selected.value = clean.toSet()
    }

    fun generateRecipes() {
        val chosen = _selected.value.toList()
        val use = if (chosen.isNotEmpty()) chosen else _ingredients.value
        if (use.isEmpty()) return

        viewModelScope.launch {
            _loading.value = true
            try {
                val result: List<Recipe> = repo.suggestRecipesFrom(use)
                _current.value = result
                _history.value = listOf(result) + _history.value
            } finally {
                _loading.value = false
            }
        }
    }
}
