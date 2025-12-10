package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun SavedRecipesScreen(
    vm: SeeFoodViewModel,
    onHome: () -> Unit,
    onRecipes: () -> Unit,
    onNutrition: () -> Unit,
    onProfile: () -> Unit
) {
    val recipes by vm.savedRecipes.collectAsState()

    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)
    val textDark = Color(0xFF111827)

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
        ) {
            Text(
                text = "Saved Recipes",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = textDark
            )

            Spacer(Modifier.height(8.dp))

            if (recipes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You haven't saved any recipes yet.",
                        color = Color(0xFF6B7280)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp) // space for bottom bar
                ) {
                    items(recipes) { recipe ->
                        SavedRecipeCard(
                            recipe = recipe,
                            brandGreen = brandGreen,
                            onUnsave = { vm.removeSavedRecipe(recipe) }
                        )
                    }
                }
            }
        }

        // Shared bottom navigation bar
        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            brandGreen = brandGreen,
            current = BottomNavDestination.Recipes,
            onHome = onHome,
            onRecipes = onRecipes,
            onNutrition = onNutrition,
            onProfile = onProfile
        )
    }
}

@Composable
private fun SavedRecipeCard(
    recipe: Recipe,
    brandGreen: Color,
    onUnsave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = recipe.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.labelMedium,
                color = brandGreen
            )

            Spacer(Modifier.height(4.dp))

            recipe.ingredients.forEach { ingredient ->
                Text(
                    text = "• $ingredient",
                    fontSize = 14.sp,
                    color = Color(0xFF4B5563)
                )
            }

            Spacer(Modifier.height(8.dp))
            Divider(color = Color(0xFFE5E7EB))
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Steps",
                style = MaterialTheme.typography.labelMedium,
                color = brandGreen
            )

            Spacer(Modifier.height(4.dp))

            recipe.steps.forEachIndexed { index, step ->
                Text(
                    text = "${index + 1}. $step",
                    fontSize = 14.sp,
                    color = Color(0xFF4B5563)
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onUnsave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Remove from Saved",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

