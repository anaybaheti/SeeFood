package com.cs407.seefood.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import com.cs407.seefood.network.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeeFoodApp(vm: SeeFoodViewModel) {
    val ingredients by vm.ingredients.collectAsState()
    val recipes by vm.recipes.collectAsState()          // Expect: List<Recipe>?
    val loading by vm.loading.collectAsState()
    val history by vm.history.collectAsState()          // Expect: List<List<Recipe>>?

    Scaffold(
        topBar = {
            // Use a Material3 top app bar that exists across versions
            CenterAlignedTopAppBar(title = { Text("SeeFood") })
        }
    ) { inner ->   // <-- content lambda is part of Scaffold call
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // simple manual text entry as a fallback while camera is unavailable
            var text by rememberSaveable { mutableStateOf(ingredients.joinToString(", ")) }

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Ingredients (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    vm.setIngredients(
                        text.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                    )
                    vm.generateRecipes()
                },
                enabled = !loading
            ) {
                Text(if (loading) "Thinking..." else "See Recipes")
            }

            Divider()

            // CURRENT
            Text("Current Suggestions", style = MaterialTheme.typography.titleMedium)
            val current: List<Recipe> = recipes ?: emptyList()
            if (current.isEmpty()) {
                Text("No recipes yet. Add ingredients and tap See Recipes.")
            } else {
                RecipeList(current)
            }

            Spacer(Modifier.height(16.dp))

            // HISTORY
            Text("Past Suggestions", style = MaterialTheme.typography.titleMedium)
            // Expecting history to be List<List<Recipe>>.
            // If your ViewModel stores something else, map it to List<Recipe> before calling RecipeList.
            if (history.isEmpty()) {
                Text("No history yet.")
            } else {
                history.forEach { past: List<Recipe> ->
                    Divider(Modifier.padding(vertical = 6.dp))
                    RecipeList(past)
                }
            }
        }
    }
}

@Composable
private fun RecipeList(recipes: List<Recipe>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        recipes.forEach { recipe ->
            ElevatedCard {
                Column(Modifier.padding(12.dp)) {
                    Text(recipe.title, style = MaterialTheme.typography.titleMedium)

                    if (recipe.ingredients.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text("Ingredients:", style = MaterialTheme.typography.labelLarge)
                        Text("• " + recipe.ingredients.joinToString("\n• "))
                    }

                    if (recipe.steps.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text("Steps:", style = MaterialTheme.typography.labelLarge)
                        Text(
                            recipe.steps
                                .mapIndexed { i, s -> "${i + 1}. $s" }
                                .joinToString("\n")
                        )
                    }
                }
            }
        }
    }
}
