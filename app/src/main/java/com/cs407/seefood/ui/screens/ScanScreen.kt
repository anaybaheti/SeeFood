package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScanScreen(onConfirm: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Camera Scan (stub)", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Text("Progress: [■■■■□□□□] 50%  •  Scan Complete (placeholder)")
        Spacer(Modifier.height(24.dp))
        Button(onClick = onConfirm) { Text("Confirm Ingredients") }
    }
}


