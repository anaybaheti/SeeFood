package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.seefood.ui.SeeFoodViewModel
import com.cs407.seefood.ui.camera.CameraPreview

@Composable
fun ScanScreen(
    vm: SeeFoodViewModel = viewModel()
) {
    val scanning by vm.scanning.collectAsState()
    val ingredients by vm.ingredients.collectAsState()

    Column(Modifier.fillMaxSize()) {

        // Camera or placeholder
        if (scanning) {
            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onIngredients = { detected -> vm.onScanUpdate(detected) }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Scan Complete")
            }
        }

        // Live preview of detected ingredients
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Detected Ingredients", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (ingredients.isEmpty()) {
                Text("Point the camera at food to begin…")
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ingredients) { ing ->
                        AssistChip(
                            onClick = { /* optional */ },
                            label = { Text(ing) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { vm.setScanning(true) }) { Text("Rescan") }
                Button(onClick = { vm.setScanning(false) }) { Text("Stop") }
            }
        }
    }
}
