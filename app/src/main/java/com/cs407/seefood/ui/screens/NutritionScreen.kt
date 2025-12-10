package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.seefood.ui.SeeFoodViewModel
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun NutritionScreen(
    vm: SeeFoodViewModel,
    onHome: () -> Unit,
    onRecipes: () -> Unit,
    onNutrition: () -> Unit,
    onProfile: () -> Unit
) {
    val meals = vm.meals

    var showAddDialog by remember { mutableStateOf(false) }

    val calories = vm.totalCalories
    val protein = vm.totalProtein
    val carbs = vm.totalCarbs
    val fat = vm.totalFat

    val calorieGoal = 2000f
    val caloriesProgress = (calories / calorieGoal).coerceIn(0f, 1f)

    val proteinGoal = 150f
    val proteinProgress = (protein / proteinGoal).coerceIn(0f, 1f)

    val carbsGoal = 250f
    val carbsProgress = (carbs / carbsGoal).coerceIn(0f, 1f)

    val fatGoal = 65f
    val fatProgress = (fat / fatGoal).coerceIn(0f, 1f)

    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)
    val textDark = Color(0xFF111827)

    // 🔔 Auto-reset at local midnight every day (while this screen is in memory)
    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            val cal = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                // move to next midnight
                add(Calendar.DAY_OF_YEAR, 1)
            }
            val delayMillis = cal.timeInMillis - now
            if (delayMillis > 0) {
                delay(delayMillis)
            }
            vm.resetTodayNutrition()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(lightTop, Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Nutrition Log",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textDark
                )
                Text(
                    text = "Track your daily intake",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
            }

            // Daily overview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = "Daily Overview",
                        style = MaterialTheme.typography.titleMedium,
                        color = textDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Calories ring
                        Box(
                            modifier = Modifier.size(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { caloriesProgress },
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 12.dp,
                                color = brandGreen,
                                trackColor = Color(0xFFE5E7EB)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = calories.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textDark
                                )
                                Text(
                                    text = "/ 2,000 cal",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        // Macros
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MacroRow(
                                label = "Protein",
                                value = "${protein}g / 150g",
                                progress = proteinProgress,
                                barColor = Color(0xFF2563EB)
                            )
                            MacroRow(
                                label = "Carbs",
                                value = "${carbs}g / 250g",
                                progress = carbsProgress,
                                barColor = Color(0xFFF97316)
                            )
                            MacroRow(
                                label = "Fat",
                                value = "${fat}g / 65g",
                                progress = fatProgress,
                                barColor = Color(0xFFF59E0B)
                            )
                        }
                    }
                }
            }

            // Add Meal button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = brandGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "+  Add Meal")
                }
            }

            // Manual reset for today
            TextButton(
                onClick = { vm.resetTodayNutrition() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Reset today",
                    color = Color(0xFF6B7280)
                )
            }

            // Recent meals header
            Text(
                text = "Recent Meals",
                style = MaterialTheme.typography.titleMedium,
                color = textDark
            )

            // Meals list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(meals, key = { it.id }) { meal ->
                    MealRow(
                        label = meal.name,
                        time = meal.time,
                        calories = meal.calories,
                        highlightColor = brandGreen
                    )
                }
            }
        }

        // Add-meal dialog
        if (showAddDialog) {
            AddMealDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, cal, prot, carb, fat ->
                    vm.addMealFromUser(
                        name = name,
                        calories = cal,
                        protein = prot,
                        carbs = carb,
                        fat = fat
                    )
                    showAddDialog = false
                }
            )
        }

        // Bottom nav
        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            brandGreen = brandGreen,
            current = BottomNavDestination.Nutrition,
            onHome = onHome,
            onRecipes = onRecipes,
            onNutrition = onNutrition,
            onProfile = onProfile
        )
    }
}

@Composable
private fun AddMealDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, calories: Int, protein: Int, carbs: Int, fat: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Meal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Meal name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Calories") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Protein (g)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Carbs (g)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = fat,
                    onValueChange = { fat = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Fat (g)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cal = calories.toIntOrNull() ?: 0
                    val prot = protein.toIntOrNull() ?: 0
                    val carb = carbs.toIntOrNull() ?: 0
                    val f = fat.toIntOrNull() ?: 0
                    onSave(name, cal, prot, carb, f)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun MacroRow(
    label: String,
    value: String,
    progress: Float,
    barColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4B5563)
            )
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = barColor,
            trackColor = Color(0xFFE5E7EB)
        )
    }
}

@Composable
private fun MealRow(
    label: String,
    time: String,
    calories: Int,
    highlightColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111827)
                )
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
            Text(
                text = "$calories cal",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = highlightColor
            )
        }
    }
}
