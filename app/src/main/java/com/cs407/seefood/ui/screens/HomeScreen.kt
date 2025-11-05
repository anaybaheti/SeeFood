package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onScan: () -> Unit,
    onRecipes: () -> Unit,
    onNutrition: () -> Unit,
    onProfile: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onScan, modifier = Modifier.fillMaxWidth()) { Text("Scan Food") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRecipes, modifier = Modifier.fillMaxWidth()) { Text("Recipe Suggestions") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onNutrition, modifier = Modifier.fillMaxWidth()) { Text("Nutrition Log") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onProfile, modifier = Modifier.fillMaxWidth()) { Text("Profile & Settings") }
    }
}

