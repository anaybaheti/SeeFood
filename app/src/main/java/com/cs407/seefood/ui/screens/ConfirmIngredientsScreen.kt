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
fun ConfirmIngredientsScreen(
    onDone: () -> Unit,
    vm: SeeFoodViewModel = viewModel()
) {
    val ingredients by vm.ingredients.collectAsState()
    val selected by vm.selected.collectAsState()

    var manual by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Confirm Ingredients", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ingredients) { ing ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(ing, style = MaterialTheme.typography.bodyLarge)
                    Checkbox(
                        checked = selected.contains(ing),
                        onCheckedChange = { vm.toggleIngredient(ing) }
                    )
                }
            }
        }

        OutlinedTextField(
            value = manual,
            onValueChange = { manual = it },
            label = { Text("Add manually") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                if (manual.isNotBlank()) {
                    vm.addManual(manual)
                    manual = ""
                }
            }) { Text("Add") }

            Spacer(Modifier.weight(1f))

            Button(onClick = {
                vm.generateRecipes()
                onDone()
            }) { Text("See Recipes") }
        }
    }
}
