package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmIngredientsScreen(onDone: () -> Unit) {
    var items by remember { mutableStateOf(listOf("apple","milk","bread")) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Confirm Ingredients", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        items.forEach { Text("• $it") }
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = { items = items + "manual_item" }) { Text("Add Manually") }
            Spacer(Modifier.width(12.dp))
            Button(onClick = onDone) { Text("Done") }
        }
    }
}


