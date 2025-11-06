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

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * While developing on laptop, this mock lets you test flows.
 * Later, swap the body for a real CameraX preview and call onIngredients(detectedList).
 */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onIngredients: (List<String>) -> Unit
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Camera preview (mock)")
            Spacer(Modifier.height(12.dp))
            Button(onClick = { onIngredients(listOf("tomato", "onion", "garlic")) }) {
                Text("Mock detect: tomato, onion, garlic")
            }
        }
    }
}
