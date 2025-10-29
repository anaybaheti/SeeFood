package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Profile & Settings (placeholder)", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("Saved recipes, reminders, and preferences will live here.")
    }
}


