package com.cs407.seefood.ui.camera

import android.Manifest
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.cs407.seefood.ml.IngredientAnalyzer
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onIngredients: (List<String>) -> Unit,
    onAddManual: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Camera permission
    var hasPermission by remember { mutableStateOf(false) }
    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        requestPermission.launch(Manifest.permission.CAMERA)
    }

    // Camera controller (NO PREVIEW FLAG — not a thing)
    val controller = remember(context) {
        LifecycleCameraController(context).apply {
            // Enable analysis so we can run ML on frames
            setEnabledUseCases(LifecycleCameraController.IMAGE_ANALYSIS)
        }
    }

    // Bind controller to lifecycle
    LaunchedEffect(lifecycleOwner) {
        controller.bindToLifecycle(lifecycleOwner)
    }

    // Optional: hook up your analyzer to emit ingredient lists
    val analyzer = remember {
        IngredientAnalyzer { list -> onIngredients(list) }
    }
    DisposableEffect(controller) {
        val exec = Executors.newSingleThreadExecutor()
        controller.setImageAnalysisAnalyzer(exec) { image: ImageProxy ->
            analyzer.analyze(image)
        }
        onDispose {
            controller.clearImageAnalysisAnalyzer()
            exec.shutdown()
        }
    }

    Column(modifier) {
        // PreviewView hosted in Compose
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FIT_CENTER
                    this.controller = controller
                }
            }
        )

        // Simple manual-add controls (example)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { onAddManual("tomato") }) { Text("Add tomato") }
            Button(onClick = { onAddManual("onion") }) { Text("Add onion") }
            Button(onClick = { onAddManual("garlic") }) { Text("Add garlic") }
        }
    }
}
