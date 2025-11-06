package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.seefood.network.Recipe
import com.cs407.seefood.ui.SeeFoodViewModel

@Composable
fun RecipesScreen(vm: SeeFoodViewModel) {
    val loading by vm.loading.collectAsState()
    val current by vm.current.collectAsState()
    val history by vm.history.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Recipe Suggestions", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }

        Text("Now", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (current.isEmpty()) Text("No suggestions yet — add ingredients and tap See Recipes.")
        else RecipeList(current)

        Spacer(Modifier.height(16.dp))
        Divider()
        Spacer(Modifier.height(16.dp))

        Text("Past Suggestions", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (history.isEmpty()) Text("No history yet.")
        else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history) { snapshot -> RecipeList(snapshot) }
            }
        }
    }
}

@Composable
private fun RecipeList(recipes: List<Recipe>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        recipes.forEach { r ->
            ElevatedCard {
                Column(Modifier.padding(12.dp)) {
                    Text(r.title, style = MaterialTheme.typography.titleMedium)
                    if (r.ingredients.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text("Ingredients:", style = MaterialTheme.typography.labelLarge)
                        Text("• " + r.ingredients.joinToString("\n• "))
                    }
                    if (r.steps.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text("Steps:", style = MaterialTheme.typography.labelLarge)
                        Text(r.steps.mapIndexed { i, s -> "${i + 1}. $s" }.joinToString("\n"))
                    }
                }
            }
        }
    }
}
