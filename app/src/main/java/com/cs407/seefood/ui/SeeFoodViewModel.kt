package com.cs407.seefood.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.seefood.data.SeeFoodRepository
import com.cs407.seefood.network.Recipe
import com.cs407.seefood.notifications.DailyReminderWorker
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

// Simple model for the profile’s daily goals
data class DailyGoals(
    val calories: Int = 2000,
    val proteinGrams: Int = 150,
    val waterGlasses: Int = 8
)

class SeeFoodViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = SeeFoodRepository()

    //  FIRESTORE (points to database ID "seefood1")
    private val db: FirebaseFirestore =
        FirebaseFirestore.getInstance(FirebaseApp.getInstance(), "seefood1")

    //  USER PROFILE STATE

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

        loadSavedRecipesForEmail(mail)
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

    //  DAILY GOALS / SETTINGS STATE

    private val _dailyGoals = MutableStateFlow(DailyGoals())
    val dailyGoals: StateFlow<DailyGoals> = _dailyGoals

    private val _remindersEnabled = MutableStateFlow(false)
    val remindersEnabled: StateFlow<Boolean> = _remindersEnabled

    private val _darkModeEnabled = MutableStateFlow(false)
    val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled

    fun updateDailyGoals(calories: Int, proteinGrams: Int, waterGlasses: Int) {
        _dailyGoals.value = DailyGoals(calories, proteinGrams, waterGlasses)
    }

    fun setRemindersEnabled(enabled: Boolean) {
        _remindersEnabled.value = enabled
        val ctx = getApplication<Application>().applicationContext
        if (enabled) {
            scheduleDailyReminder(ctx)
        } else {
            cancelDailyReminder(ctx)
        }
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        _darkModeEnabled.value = enabled
    }

    private fun scheduleDailyReminder(context: Context) {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 12)   // 12:00 noon
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now) {
                // if noon already passed, schedule for tomorrow
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val initialDelay = calendar.timeInMillis - now

        val request =
            PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_REMINDER_WORK_NAME)
    }

    //  INGREDIENTS / RECIPES STATE

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

    // saved recipes for this user (persisted in Firestore)
    private val _savedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val savedRecipes: StateFlow<List<Recipe>> = _savedRecipes

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    //  ACTIONS

    private fun loadSavedRecipesForEmail(mail: String) {
        if (mail.isBlank()) return

        db.collection("savedRecipes")
            .document(mail)
            .collection("items")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getString("id") ?: doc.id
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val imageUrl = doc.getString("imageUrl")
                    @Suppress("UNCHECKED_CAST")
                    val ingredients =
                        (doc.get("ingredients") as? List<*>)?.filterIsInstance<String>()
                            ?: emptyList()
                    @Suppress("UNCHECKED_CAST")
                    val steps =
                        (doc.get("steps") as? List<*>)?.filterIsInstance<String>()
                            ?: emptyList()

                    Recipe(
                        id = id,
                        title = title,
                        imageUrl = imageUrl,
                        ingredients = ingredients,
                        steps = steps
                    )
                }
                _savedRecipes.value = list
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    fun refreshSavedRecipes() {
        val mail = email ?: return
        loadSavedRecipesForEmail(mail)
    }

    private fun recipeDocId(recipe: Recipe): String =
        if (recipe.id.isNotBlank()) recipe.id else recipe.title

    fun saveRecipeForCurrentUser(recipe: Recipe) {
        val mail = email ?: return

        // Avoid duplicates locally
        if (_savedRecipes.value.any { it.id == recipe.id }) return
        _savedRecipes.value = _savedRecipes.value + recipe

        val data = hashMapOf(
            "id" to recipe.id,
            "title" to recipe.title,
            "imageUrl" to recipe.imageUrl,
            "ingredients" to recipe.ingredients,
            "steps" to recipe.steps
        )

        db.collection("savedRecipes")
            .document(mail)
            .collection("items")
            .document(recipeDocId(recipe))
            .set(data)
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    fun removeSavedRecipe(recipe: Recipe) {
        val mail = email ?: return

        _savedRecipes.value = _savedRecipes.value.filterNot { it.id == recipe.id }

        db.collection("savedRecipes")
            .document(mail)
            .collection("items")
            .document(recipeDocId(recipe))
            .delete()
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

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

    companion object {
        private const val DAILY_REMINDER_WORK_NAME = "daily_goals_reminder"
    }
}
