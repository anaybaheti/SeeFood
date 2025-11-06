//package com.cs407.seefood.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.cs407.seefood.ui.SeeFoodViewModel
//import com.cs407.seefood.ui.camera.CameraPreview
//
//@Composable
//fun ScanScreen(
//    vm: SeeFoodViewModel = viewModel(),
//    onConfirm: () -> Unit
//) {
//    val scanning by vm.scanning.collectAsState()
//    val ingredients by vm.ingredients.collectAsState()
//
//    Column(Modifier.fillMaxSize()) {
//
//        // Camera (or placeholder on laptop/emulator)
//        if (scanning) {
//            CameraPreview(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f),
//                onIngredients = { detected -> vm.onScanUpdate(detected) }
//            )
//        } else {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f),
//                contentAlignment = Alignment.Center
//            ) { Text("Scan Complete") }
//        }
//
//        // Live preview + actions
//        Column(Modifier.fillMaxWidth().padding(16.dp)) {
//            Text("Detected Ingredients", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
//            Spacer(Modifier.height(8.dp))
//            if (ingredients.isEmpty()) {
//                Text("Point the camera at food or use the mock button.")
//            } else {
//                Text(ingredients.joinToString(", "))
//            }
//
//            Spacer(Modifier.height(14.dp))
//            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//                Button(onClick = { vm.setScanning(true) }) { Text("Rescan") }
//                Button(onClick = { vm.setScanning(false) }) { Text("Stop") }
//                Spacer(Modifier.weight(1f))
//                Button(
//                    enabled = ingredients.isNotEmpty(),
//                    onClick = {
//                        vm.setScanning(false)
//                        onConfirm()
//                    }
//                ) { Text("Confirm Ingredients") }
//            }
//        }
//    }
//}


package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.seefood.ui.SeeFoodViewModel
import com.cs407.seefood.ui.camera.CameraPreview

@Composable
fun ScanScreen(
    vm: SeeFoodViewModel = viewModel(),
    onConfirm: () -> Unit = {}
) {
    // Ingredients list that the VM is holding
    val ingredients by vm.ingredients.collectAsState()

    // Local toggle to show/hide the live camera preview
    var showCamera by remember { mutableStateOf(true) }

    Column(Modifier.fillMaxSize()) {

        // --- Camera (or a simple placeholder when hidden) ---
        if (showCamera) {
            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                // If you later wire ML detection, call this with the detected list
                onIngredients = { detected -> vm.onScanUpdate(detected) },
                // Manual add from the text box at the bottom of CameraPreview
                onAddManual = { txt -> vm.addManual(txt) }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Scan paused", style = MaterialTheme.typography.bodyMedium)
            }
        }

        // --- Live preview of the current list + actions ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "Detected / Added Ingredients",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            if (ingredients.isEmpty()) {
                Text(
                    text = "No ingredients yet — point the camera at food labels/items or add manually.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = ingredients.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { showCamera = true }) { Text("Rescan") }
                Button(onClick = { showCamera = false }) { Text("Stop") }

                Spacer(Modifier.weight(1f))

                Button(
                    enabled = ingredients.isNotEmpty(),
                    onClick = {
                        showCamera = false
                        onConfirm()
                    }
                ) { Text("Confirm Ingredients") }
            }
        }
    }
}

