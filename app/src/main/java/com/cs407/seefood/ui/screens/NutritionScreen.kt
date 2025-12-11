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

    // NEW: Load daily goals dynamically
    val goals by vm.dailyGoals.collectAsState()

    val caloriesProgress = (calories / goals.calories.toFloat()).coerceIn(0f, 1f)
    val proteinProgress  = (protein / goals.proteinGrams.toFloat()).coerceIn(0f, 1f)
    val carbsProgress    = (carbs / goals.carbsGrams.toFloat()).coerceIn(0f, 1f)
    val fatProgress      = (fat / goals.fatGrams.toFloat()).coerceIn(0f, 1f)

    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)
    val textDark = Color(0xFF111827)

    // Auto-reset at midnight
    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            val cal = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_YEAR, 1)
            }
            delay(cal.timeInMillis - now)
            vm.resetTodayNutrition()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(lightTop, Color.White)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text("Nutrition Log", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = textDark)
            Text("Track your daily intake", color = Color(0xFF6B7280))

            // DAILY OVERVIEW CARD
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Daily Overview", style = MaterialTheme.typography.titleMedium)

                    Row(
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
                                Text(calories.toString(), fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                                Text("/ ${goals.calories} cal", fontSize = 13.sp, color = Color(0xFF6B7280))
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            MacroRow(
                                label = "Protein",
                                value = "${protein}g / ${goals.proteinGrams}g",
                                progress = proteinProgress,
                                barColor = Color(0xFF2563EB)
                            )
                            MacroRow(
                                label = "Carbs",
                                value = "${carbs}g / ${goals.carbsGrams}g",
                                progress = carbsProgress,
                                barColor = Color(0xFFF97316)
                            )
                            MacroRow(
                                label = "Fat",
                                value = "${fat}g / ${goals.fatGrams}g",
                                progress = fatProgress,
                                barColor = Color(0xFFF59E0B)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(brandGreen, Color.White)
            ) {
                Text("+  Add Meal")
            }

            TextButton(
                onClick = { vm.resetTodayNutrition() },
                modifier = Modifier.align(Alignment.End)
            ) { Text("Reset today", color = Color(0xFF6B7280)) }

            Text("Recent Meals", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(meals, key = { it.id }) { meal ->
                    MealRow(meal.name, meal.time, meal.calories, brandGreen)
                }
            }
        }

        if (showAddDialog) {
            AddMealDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name, cal, prot, carb, fat ->
                    vm.addMealFromUser(name, cal, prot, carb, fat)
                    showAddDialog = false
                }
            )
        }

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
    onSave: (String, Int, Int, Int, Int) -> Unit
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
                OutlinedTextField(name, { name = it }, label = { Text("Meal name") })
                OutlinedTextField(calories, { calories = it.filter(Char::isDigit) }, label = { Text("Calories") })
                OutlinedTextField(protein, { protein = it.filter(Char::isDigit) }, label = { Text("Protein (g)") })
                OutlinedTextField(carbs, { carbs = it.filter(Char::isDigit) }, label = { Text("Carbs (g)") })
                OutlinedTextField(fat, { fat = it.filter(Char::isDigit) }, label = { Text("Fat (g)") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    name,
                    calories.toIntOrNull() ?: 0,
                    protein.toIntOrNull() ?: 0,
                    carbs.toIntOrNull() ?: 0,
                    fat.toIntOrNull() ?: 0
                )
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun MacroRow(label: String, value: String, progress: Float, barColor: Color) {
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(label, fontSize = 13.sp, color = Color(0xFF6B7280))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = barColor,
            trackColor = Color(0xFFE5E7EB)
        )
    }
}

@Composable
private fun MealRow(label: String, time: String, calories: Int, highlightColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Text(time, fontSize = 12.sp, color = Color(0xFF6B7280))
            }
            Text("$calories cal", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = highlightColor)
        }
    }
}
