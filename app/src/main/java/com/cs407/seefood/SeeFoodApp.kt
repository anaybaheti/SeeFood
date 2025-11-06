//package com.cs407.seefood.ui
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.cs407.seefood.network.Recipe
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SeeFoodApp(vm: SeeFoodViewModel = viewModel()) {
//    val ingredients by vm.ingredients.collectAsState()
//    val loading by vm.loading.collectAsState()
//    val current by vm.current.collectAsState()   // List<Recipe>
//    val history by vm.history.collectAsState()   // List<List<Recipe>>
//
//    Scaffold(
//        topBar = { TopAppBar(title = { Text("SeeFood") }) }
//    ) { inner ->
//        Column(
//            modifier = Modifier
//                .padding(inner)
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            // Manual entry fallback for laptop (no camera)
//            var text by rememberSaveable { mutableStateOf(ingredients.joinToString(", ")) }
//
//            OutlinedTextField(
//                value = text,
//                onValueChange = { text = it },
//                label = { Text("Ingredients (comma separated)") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Button(
//                onClick = {
//                    vm.setIngredients(
//                        text.split(",").map { it.trim() }.filter { it.isNotEmpty() }
//                    )
//                    vm.generateRecipes()
//                },
//                enabled = !loading
//            ) {
//                Text(if (loading) "Thinking..." else "See Recipes")
//            }
//
//            Divider()
//
//            Text("Current Suggestions", style = MaterialTheme.typography.titleMedium)
//            if (current.isEmpty()) {
//                Text("No recipes yet. Add ingredients and tap See Recipes.")
//            } else {
//                RecipeList(current)
//            }
//
//            Spacer(Modifier.height(16.dp))
//            Text("Past Suggestions", style = MaterialTheme.typography.titleMedium)
//            if (history.isEmpty()) {
//                Text("No history yet.")
//            } else {
//                history.forEach { batch ->
//                    Divider(Modifier.padding(vertical = 6.dp))
//                    RecipeList(batch)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun RecipeList(recipes: List<Recipe>) {
//    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//        recipes.forEach { recipe ->
//            ElevatedCard {
//                Column(Modifier.padding(12.dp)) {
//                    Text(recipe.title, style = MaterialTheme.typography.titleMedium)
//
//                    if (recipe.ingredients.isNotEmpty()) {
//                        Spacer(Modifier.height(6.dp))
//                        Text("Ingredients:", style = MaterialTheme.typography.labelLarge)
//                        Text("• " + recipe.ingredients.joinToString("\n• "))
//                    }
//                    if (recipe.steps.isNotEmpty()) {
//                        Spacer(Modifier.height(6.dp))
//                        Text("Steps:", style = MaterialTheme.typography.labelLarge)
//                        Text(recipe.steps.mapIndexed { i, s -> "${i + 1}. $s" }.joinToString("\n"))
//                    }
//                }
//            }
//        }
//    }
//}

// SeeFoodApp.kt
package com.cs407.seefood.ui

import androidx.compose.runtime.Composable
import com.cs407.seefood.ui.SeeFoodViewModel
import com.cs407.seefood.ui.navigation.SeeFoodNavHost

@Composable
fun SeeFoodApp(vm: SeeFoodViewModel) {
    SeeFoodNavHost(vm = vm)
}

