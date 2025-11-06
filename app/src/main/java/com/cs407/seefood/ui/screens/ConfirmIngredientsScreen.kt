//package com.cs407.seefood.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.cs407.seefood.ui.SeeFoodViewModel
//
//@Composable
//fun ConfirmIngredientsScreen(
//    vm: SeeFoodViewModel,
//    onDone: () -> Unit = {}
//) {
//    val ingredients by vm.ingredients.collectAsState()
//    val selected by vm.selected.collectAsState()
//    var manual by remember { mutableStateOf("") }
//
//    Column(Modifier.fillMaxSize().padding(16.dp)) {
//        Text("Confirm Ingredients", style = MaterialTheme.typography.headlineSmall)
//        Spacer(Modifier.height(12.dp))
//
//        LazyColumn(Modifier.weight(1f)) {
//            items(ingredients) { ing ->
//                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                    Text(ing, style = MaterialTheme.typography.bodyLarge)
//                    Checkbox(checked = selected.contains(ing), onCheckedChange = { vm.toggleIngredient(ing) })
//                }
//            }
//        }
//
//        OutlinedTextField(
//            value = manual,
//            onValueChange = { manual = it },
//            label = { Text("Add manually") },
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(8.dp))
//        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
//            Button(onClick = {
//                if (manual.isNotBlank()) { vm.addManual(manual); manual = "" }
//            }) { Text("Add") }
//            Spacer(Modifier.weight(1f))
//            Button(onClick = { vm.generateRecipes(); onDone() }) { Text("See Recipes") }
//        }
//    }
//}

package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.seefood.ui.SeeFoodViewModel

@Composable
fun ConfirmIngredientsScreen(
    vm: SeeFoodViewModel = viewModel(),
    onDone: () -> Unit = {}
) {
    val ingredients by vm.ingredients.collectAsState()
    val loading by vm.loading.collectAsState()

    var manualText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simple title instead of TopAppBar
        Text(
            text = "Confirm Ingredients",
            style = MaterialTheme.typography.titleLarge
        )

        // Current list (or hint)
        if (ingredients.isEmpty()) {
            Text(
                text = "No ingredients yet — add manually below or go back to scan.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ingredients, key = { it }) { ing ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ing,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(
                            onClick = {
                                val newList = ingredients.toMutableList().apply { remove(ing) }
                                vm.onScanUpdate(newList)
                            }
                        ) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Remove")
                        }
                    }
                }
            }
        }

        // Manual add row (no KeyboardOptions/Actions)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = manualText,
                onValueChange = { manualText = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Add manually") }
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    val t = manualText.trim()
                    if (t.isNotEmpty()) {
                        vm.addManual(t)
                        manualText = ""
                    }
                }
            ) {
                Text("Add")
            }
        }

        // Primary action
        Button(
            onClick = {
                vm.generateRecipes()   // no lambda arg → fixes “too many arguments”
                onDone()               // navigate afterward if you want
            },
            enabled = ingredients.isNotEmpty() && !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Generating…" else "See Recipes")
        }
    }
}
