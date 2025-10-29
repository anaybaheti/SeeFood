package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecipesScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Recipes (placeholder)", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("• Peanut Butter Toast\n• Apple Oatmeal\n• Veggie Omelette")
    }
}


