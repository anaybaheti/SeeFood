package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.seefood.ui.SeeFoodViewModel

@Composable
fun RecipesScreen(vm: SeeFoodViewModel = viewModel()) {
    val loading by vm.loading.collectAsState()
    val current by vm.recipes.collectAsState()
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

        if (current == null) {
            Text("No suggestions yet — add ingredients and tap See Recipes.")
        } else {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    // Render whatever your model is; toString() keeps it version-agnostic
                    Text(current.toString())
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Divider()
        Spacer(Modifier.height(12.dp))

        Text("Past Suggestions", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (history.isEmpty()) {
            Text("No history yet.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(history) { snap ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) {
                            Text(snap.toString())
                        }
                    }
                }
            }
        }
    }
}
