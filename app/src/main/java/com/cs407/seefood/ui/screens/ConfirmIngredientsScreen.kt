//package com.cs407.seefood.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.cs407.seefood.ui.SeeFoodViewModel
//
//@Composable
//fun ConfirmIngredientsScreen(
//    vm: SeeFoodViewModel,
//    onDone: () -> Unit = {}
//) {
//    val ingredients by vm.ingredients.collectAsState()
//    val selected by vm.selected.collectAsState()
//    var manual by remember { mutableStateOf("") }
//
//    Column(Modifier.fillMaxSize().padding(16.dp)) {
//        Text("Confirm Ingredients", style = MaterialTheme.typography.headlineSmall)
//        Spacer(Modifier.height(12.dp))
//
//        LazyColumn(Modifier.weight(1f)) {
//            items(ingredients) { ing ->
//                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                    Text(ing, style = MaterialTheme.typography.bodyLarge)
//                    Checkbox(checked = selected.contains(ing), onCheckedChange = { vm.toggleIngredient(ing) })
//                }
//            }
//        }
//
//        OutlinedTextField(
//            value = manual,
//            onValueChange = { manual = it },
//            label = { Text("Add manually") },
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(8.dp))
//        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
//            Button(onClick = {
//                if (manual.isNotBlank()) { vm.addManual(manual); manual = "" }
//            }) { Text("Add") }
//            Spacer(Modifier.weight(1f))
//            Button(onClick = { vm.generateRecipes(); onDone() }) { Text("See Recipes") }
//        }
//    }
//}

//package com.cs407.seefood.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.outlined.Delete
//import androidx.compose.material3.Button
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.cs407.seefood.ui.SeeFoodViewModel
//
//@Composable
//fun ConfirmIngredientsScreen(
//    vm: SeeFoodViewModel = viewModel(),
//    onDone: () -> Unit = {}
//) {
//    val ingredients by vm.ingredients.collectAsState()
//    val loading by vm.loading.collectAsState()
//
//    var manualText by rememberSaveable { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        // Simple title instead of TopAppBar
//        Text(
//            text = "Confirm Ingredients",
//            style = MaterialTheme.typography.titleLarge
//        )
//
//        // Current list (or hint)
//        if (ingredients.isEmpty()) {
//            Text(
//                text = "No ingredients yet — add manually below or go back to scan.",
//                style = MaterialTheme.typography.bodyMedium
//            )
//        } else {
//            LazyColumn(
//                modifier = Modifier
//                    .weight(1f, fill = true)
//                    .fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(ingredients, key = { it }) { ing ->
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = ing,
//                            modifier = Modifier.weight(1f),
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                        IconButton(
//                            onClick = {
//                                val newList = ingredients.toMutableList().apply { remove(ing) }
//                                vm.onScanUpdate(newList)
//                            }
//                        ) {
//                            Icon(Icons.Outlined.Delete, contentDescription = "Remove")
//                        }
//                    }
//                }
//            }
//        }
//
//        // Manual add row (no KeyboardOptions/Actions)
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            OutlinedTextField(
//                value = manualText,
//                onValueChange = { manualText = it },
//                modifier = Modifier.weight(1f),
//                singleLine = true,
//                placeholder = { Text("Add manually") }
//            )
//            Spacer(Modifier.width(8.dp))
//            Button(
//                onClick = {
//                    val t = manualText.trim()
//                    if (t.isNotEmpty()) {
//                        vm.addManual(t)
//                        manualText = ""
//                    }
//                }
//            ) {
//                Text("Add")
//            }
//        }
//
//        // Primary action
//        Button(
//            onClick = {
//                vm.generateRecipes()   // no lambda arg → fixes “too many arguments”
//                onDone()               // navigate afterward if you want
//            },
//            enabled = ingredients.isNotEmpty() && !loading,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(if (loading) "Generating…" else "See Recipes")
//        }
//    }
//}

package com.cs407.seefood.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.seefood.ui.SeeFoodViewModel

@Composable
fun ConfirmIngredientsScreen(
    vm: SeeFoodViewModel,
    onDone: () -> Unit = {}
) {
    val ingredients by vm.ingredients.collectAsState()
    val selected by vm.selected.collectAsState()
    val loading by vm.loading.collectAsState()

    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)
    val textDark = Color(0xFF111827)

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var manualName by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(lightTop, Color.White)
                )
            )
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Confirm Ingredients",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textDark
                )
                Text(
                    text = "Review detected items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
            }

            // List of ingredients
            if (ingredients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No ingredients yet — go back to scan or add manually.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ingredients, key = { it }) { ing ->
                        val isSelected = ing in selected

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { vm.toggleIngredient(ing) },
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected)
                                brandGreen.copy(alpha = 0.08f)
                            else
                                Color.White,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) brandGreen.copy(alpha = 0.6f)
                                else Color(0xFFE5E7EB)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Check circle
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .background(
                                            color = if (isSelected)
                                                brandGreen
                                            else
                                                Color(0xFFF3F4F6),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = ing.replaceFirstChar { it.uppercase() },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textDark
                                    )
                                    Text(
                                        // just a visual placeholder like in the mockup
                                        text = "1 item",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom action area
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Add manually (outlined pill)
                OutlinedButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, brandGreen),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = brandGreen
                    )
                ) {
                    Text(
                        text = "+  Add Manually",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Confirm (solid green pill)
                Button(
                    onClick = {
                        vm.generateRecipes()
                        onDone()
                    },
                    enabled = ingredients.isNotEmpty() && !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = brandGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (loading) "Generating..." else "Confirm  →",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Dialog for adding an ingredient manually
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            if (manualName.isNotBlank()) {
                                vm.addManual(manualName)
                                manualName = ""
                            }
                            showAddDialog = false
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    IconButton(onClick = { showAddDialog = false }) {
                        Icon(Icons.Filled.Close, contentDescription = "Cancel")
                    }
                },
                title = { Text("Add ingredient") },
                text = {
                    OutlinedTextField(
                        value = manualName,
                        onValueChange = { manualName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Butter") }
                    )
                }
            )
        }
    }
}
