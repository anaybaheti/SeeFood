package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NutritionScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Nutrition Log (placeholder)", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("Macros Today: Protein 80g • Carbs 220g • Fat 60g")
    }
}


