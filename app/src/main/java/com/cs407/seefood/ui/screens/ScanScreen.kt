//package com.cs407.seefood.ui.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.PhotoCamera
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.cs407.seefood.ui.SeeFoodViewModel
//import com.cs407.seefood.ui.camera.CameraPreview
//
//@Composable
//fun ScanScreen(
//    vm: SeeFoodViewModel = viewModel(),
//    onConfirm: () -> Unit = {}
//) {
//    val ingredients by vm.ingredients.collectAsState()
//    val brandGreen = Color(0xFF00C27A)
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//        // Camera feed in the background
//        CameraPreview(
//            modifier = Modifier.fillMaxSize(),
//            onIngredients = { vm.onScanUpdate(it) },
//            onAddManual = { vm.addManual(it) }
//        )
//
//        // Top gradient overlay (helps white text stand out)
//        Box(
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .fillMaxWidth()
//                .height(160.dp)
//                .background(
//                    Brush.verticalGradient(
//                        colors = listOf(Color(0x80000000), Color.Transparent)
//                    )
//                )
//        )
//
//        // Close button (visual only – hook up nav back later if you want)
//        IconButton(
//            onClick = { /* TODO: navigate back to Home if desired */ },
//            modifier = Modifier
//                .align(Alignment.TopStart)
//                .padding(16.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Filled.Close,
//                contentDescription = "Close",
//                tint = Color.White
//            )
//        }
//
//        // Floating ingredient labels in the upper half
//        if (ingredients.isNotEmpty()) {
//            Column(
//                modifier = Modifier
//                    .align(Alignment.TopStart)
//                    .padding(start = 32.dp, top = 96.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                ingredients.take(12).forEach { name ->
//                    Text(
//                        text = name.replaceFirstChar { it.uppercase() },
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Medium,
//                        color = Color.White
//                    )
//                }
//            }
//        }
//
//        // White status pill: "Detecting items..."
//        Surface(
//            shape = RoundedCornerShape(999.dp),
//            color = Color.White,
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 140.dp)
//        ) {
//            Text(
//                text = if (ingredients.isEmpty()) "Detecting items..." else "Items detected",
//                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
//                fontSize = 14.sp,
//                color = Color(0xFF111827)
//            )
//        }
//
//        // Bottom black bar
//        Box(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .fillMaxWidth()
//                .height(120.dp)
//                .background(Color.Black)
//        )
//
//        // Big green camera button overlapping the bar
//        Surface(
//            shape = CircleShape,
//            color = brandGreen,
//            tonalElevation = 8.dp,
//            shadowElevation = 12.dp,
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 24.dp)
//                .size(88.dp)
//                .clickable {
//                    // Use this as your "capture" → go to ConfirmIngredients screen
//                    onConfirm()
//                }
//        ) {
//            Box(contentAlignment = Alignment.Center) {
//                Icon(
//                    imageVector = Icons.Filled.PhotoCamera,
//                    contentDescription = "Capture",
//                    tint = Color.White,
//                    modifier = Modifier.size(36.dp)
//                )
//            }
//        }
//    }
//}
//
package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.seefood.ui.SeeFoodViewModel
import com.cs407.seefood.ui.camera.CameraPreview

@Composable
fun ScanScreen(
    vm: SeeFoodViewModel = viewModel(),
    onConfirm: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val ingredients by vm.ingredients.collectAsState()
    val brandGreen = Color(0xFF00C27A)

    Box(modifier = Modifier.fillMaxSize()) {

        // Camera feed in the background
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onIngredients = { vm.onScanUpdate(it) },
            onAddManual = { vm.addManual(it) }
        )

        // Top gradient overlay (helps white text stand out)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x80000000), Color.Transparent)
                    )
                )
        )

        // Close / Back button → go back to Home
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Back to Home",
                tint = Color.White
            )
        }

        // Floating ingredient labels in the upper half
        if (ingredients.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 32.dp, top = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ingredients.take(12).forEach { name ->
                    Text(
                        text = name.replaceFirstChar { it.uppercase() },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }

        // White status pill: "Detecting items..."
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
        ) {
            Text(
                text = if (ingredients.isEmpty()) "Detecting items..." else "Items detected",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                fontSize = 14.sp,
                color = Color(0xFF111827)
            )
        }

        // Bottom black bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.Black)
        )

        // Big green camera button overlapping the bar
        Surface(
            shape = CircleShape,
            color = brandGreen,
            tonalElevation = 8.dp,
            shadowElevation = 12.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .size(88.dp)
                .clickable {
                    // Use this as your "capture" → go to ConfirmIngredients screen
                    onConfirm()
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = "Capture",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
