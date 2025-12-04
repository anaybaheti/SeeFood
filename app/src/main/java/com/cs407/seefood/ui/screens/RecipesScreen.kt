package com.cs407.seefood.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.seefood.network.Recipe
import com.cs407.seefood.ui.SeeFoodViewModel

@Composable
fun RecipesScreen(vm: SeeFoodViewModel) {
    val loading by vm.loading.collectAsState()
    val current by vm.current.collectAsState()

    // Brand-ish colors
    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)
    val textDark = Color(0xFF111827)

    // Local screen state:
    // which recipe is currently opened in "Cook Now" detail view
    var activeRecipe by remember { mutableStateOf<Recipe?>(null) }

    // simple saved set by title (you can later connect this to the ViewModel if you want)
    val savedTitles = remember { mutableStateListOf<String>() }

    // If a recipe is active, show full "page" with that recipe
    if (activeRecipe != null) {
        RecipeDetailScreen(
            recipe = activeRecipe!!,
            brandGreen = brandGreen,
            lightTop = lightTop,
            onBack = { activeRecipe = null }
        )
        return
    }

    // Otherwise show list of recipe suggestions
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(lightTop, Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Recipe Suggestions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textDark
                )
                Text(
                    text = "Based on your ingredients",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
            }

            if (loading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(4.dp))

            if (current.isEmpty()) {
                Text(
                    text = "No suggestions yet — confirm ingredients first.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    current.forEachIndexed { index, recipe ->

                        // Dummy macro data, just to populate the UI nicely
                        val timeMinutes = listOf(15, 10, 8, 12, 20).getOrElse(index) { 15 }
                        val calories = listOf(320, 280, 350, 300, 260).getOrElse(index) { 320 }
                        val protein = listOf(18, 12, 22, 15, 10).getOrElse(index) { 18 }
                        val carbs = listOf(12, 8, 6, 10, 14).getOrElse(index) { 12 }
                        val fat = listOf(22, 20, 26, 18, 12).getOrElse(index) { 22 }

                        val isSaved = recipe.title in savedTitles

                        RecipeSuggestionCard(
                            recipe = recipe,
                            brandGreen = brandGreen,
                            timeMinutes = timeMinutes,
                            calories = calories,
                            protein = protein,
                            carbs = carbs,
                            fat = fat,
                            isSaved = isSaved,
                            onToggleSave = {
                                if (isSaved) {
                                    savedTitles.remove(recipe.title)
                                } else {
                                    savedTitles.add(recipe.title)
                                }
                            },
                            onCookNow = { activeRecipe = recipe }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RecipeSuggestionCard(
    recipe: Recipe,
    brandGreen: Color,
    timeMinutes: Int,
    calories: Int,
    protein: Int,
    carbs: Int,
    fat: Int,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onCookNow: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, brandGreen.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Top “image” area (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        color = Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // Title
                Text(
                    text = recipe.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )

                // Time row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = brandGreen
                    )
                    Text(
                        text = "$timeMinutes min",
                        fontSize = 13.sp,
                        color = brandGreen
                    )
                }

                // Macros row: Cal / Pro / Carbs / Fat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MacroBox(label = "Cal", value = calories.toString())
                    MacroBox(label = "Pro", value = "${protein}g")
                    MacroBox(label = "Carbs", value = "${carbs}g")
                    MacroBox(label = "Fat", value = "${fat}g")
                }

                // Buttons row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(999.dp),
                        border = BorderStroke(1.dp, brandGreen),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = brandGreen
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(text = if (isSaved) "Saved" else "Save")
                    }

                    Button(
                        onClick = onCookNow,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = brandGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Cook Now")
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroBox(
    label: String,
    value: String
) {
    Surface(
        modifier = Modifier
            .height(48.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF9FAFB),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF111827)
            )
        }
    }
}

/**
 * Full-page detail view for a single recipe.
 * Shown when user presses "Cook Now".
 */
@Composable
private fun RecipeDetailScreen(
    recipe: Recipe,
    brandGreen: Color,
    lightTop: Color,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(lightTop, Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top bar with back
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBack,
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF111827)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Recipe",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF111827)
                )
            }

            // Title
            Text(
                text = recipe.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )

            // Big “image”
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        color = Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(24.dp)
                    )
            )

            // Ingredients
            if (recipe.ingredients.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF111827)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        recipe.ingredients.forEach { ing ->
                            Text(
                                text = "• $ing",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF4B5563)
                            )
                        }
                    }
                }
            }

            // Steps
            if (recipe.steps.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Steps",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF111827)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recipe.steps.forEachIndexed { index, step ->
                            Text(
                                text = "${index + 1}. $step",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF4B5563)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { /* you could later hook a timer or checklist here */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandGreen,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Start Cooking")
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
