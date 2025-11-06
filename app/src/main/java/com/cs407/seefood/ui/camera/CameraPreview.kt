//package com.cs407.seefood.ui.camera
//
//import android.content.Context
//import android.util.Size
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.LifecycleOwner
//import com.cs407.seefood.ml.IngredientAnalyzer
//import java.util.concurrent.Executors
//
//@Composable
//fun CameraPreview(
//    modifier: Modifier = Modifier,
//    onIngredients: (List<String>) -> Unit
//) {
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
//    val executor = remember { Executors.newSingleThreadExecutor() }
//
//    AndroidView(
//        modifier = modifier,
//        factory = { ctx ->
//            val previewView = androidx.camera.view.PreviewView(ctx).apply {
//                scaleType = androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
//            }
//
//            val cameraProvider = cameraProviderFuture.get()
//            bindUseCases(ctx, cameraProvider, lifecycleOwner, previewView, executor, onIngredients)
//            previewView
//        },
//        update = { previewView ->
//            val cameraProvider = cameraProviderFuture.get()
//            cameraProvider.unbindAll()
//            bindUseCases(context, cameraProvider, lifecycleOwner, previewView, executor, onIngredients)
//        }
//    )
//}
//
//private fun bindUseCases(
//    context: Context,
//    cameraProvider: ProcessCameraProvider,
//    lifecycleOwner: LifecycleOwner,
//    previewView: androidx.camera.view.PreviewView,
//    executor: java.util.concurrent.Executor,
//    onIngredients: (List<String>) -> Unit
//) {
//    val preview = Preview.Builder().build().also {
//        it.setSurfaceProvider(previewView.surfaceProvider)
//    }
//
//    val analysis = ImageAnalysis.Builder()
//        .setTargetResolution(Size(1280, 720))
//        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//        .build()
//        .also {
//            it.setAnalyzer(executor, IngredientAnalyzer(onIngredients))
//        }
//
//    val selector = CameraSelector.DEFAULT_BACK_CAMERA
//
//    cameraProvider.unbindAll()
//    cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
//}

// app/src/main/java/com/cs407/seefood/ui/camera/CameraPreview.kt


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
