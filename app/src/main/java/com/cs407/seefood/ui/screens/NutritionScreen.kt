//package com.cs407.seefood.ui.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//
//@Composable
//fun NutritionScreen() {
//
//    // Mock daily nutrition data — will later come from backend
//    var calories by remember { mutableStateOf(1250) }
//    val calorieGoal = 2000f
//    val caloriesProgress = (calories / calorieGoal).coerceIn(0f, 1f)
//
//    var protein by remember { mutableStateOf(52) }
//    val proteinGoal = 150f
//    val proteinProgress = (protein / proteinGoal).coerceIn(0f, 1f)
//
//    var carbs by remember { mutableStateOf(86) }
//    val carbsGoal = 250f
//    val carbsProgress = (carbs / carbsGoal).coerceIn(0f, 1f)
//
//    var fat by remember { mutableStateOf(68) }
//    val fatGoal = 65f
//    val fatProgress = (fat / fatGoal).coerceIn(0f, 1f)
//
//    val recentMeals = listOf(
//        Triple("Breakfast - Omelette", "8:00 AM", 320),
//        Triple("Lunch - Caprese", "12:30 PM", 280),
//        Triple("Snack - Yogurt", "3:00 PM", 150)
//    )
//
//    val brandGreen = Color(0xFF00C27A)
//    val lightTop = Color(0xFFE8FFF5)
//    val textDark = Color(0xFF111827)
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(lightTop, Color.White)
//                )
//            )
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 16.dp, vertical = 24.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//
//            // Header
//            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
//                Text(
//                    text = "Nutrition Log",
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = textDark
//                )
//                Text(
//                    text = "Track your daily intake",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color(0xFF6B7280)
//                )
//            }
//
//            // Daily overview card
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(24.dp),
//                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//
//                    Text(
//                        text = "Daily Overview",
//                        style = MaterialTheme.typography.titleMedium,
//                        color = textDark
//                    )
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        // Circular calories ring
//                        Box(
//                            modifier = Modifier.size(140.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            CircularProgressIndicator(
//                                progress = { caloriesProgress },
//                                modifier = Modifier.fillMaxSize(),
//                                strokeWidth = 12.dp,
//                                color = brandGreen,
//                                trackColor = Color(0xFFE5E7EB)
//                            )
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Text(
//                                    text = calories.toString(),
//                                    fontSize = 24.sp,
//                                    fontWeight = FontWeight.SemiBold,
//                                    color = textDark
//                                )
//                                Text(
//                                    text = "/ 2,000 cal",
//                                    fontSize = 13.sp,
//                                    color = Color(0xFF6B7280)
//                                )
//                            }
//                        }
//
//                        Spacer(Modifier.width(16.dp))
//
//                        // Macro rows
//                        Column(
//                            modifier = Modifier.weight(1f),
//                            verticalArrangement = Arrangement.spacedBy(10.dp)
//                        ) {
//                            MacroRow(
//                                label = "Protein",
//                                value = "${protein}g / 150g",
//                                progress = proteinProgress,
//                                barColor = Color(0xFF2563EB)
//                            )
//                            MacroRow(
//                                label = "Carbs",
//                                value = "${carbs}g / 250g",
//                                progress = carbsProgress,
//                                barColor = Color(0xFFF97316)
//                            )
//                            MacroRow(
//                                label = "Fat",
//                                value = "${fat}g / 65g",
//                                progress = fatProgress,
//                                barColor = Color(0xFFF59E0B)
//                            )
//                        }
//                    }
//                }
//            }
//
//            // Add / Sync buttons
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                Button(
//                    onClick = {
//                        // For now, use this to "simulate" adding a meal by changing macros
//                        calories = (1100..1800).random()
//                        protein = (40..90).random()
//                        carbs = (60..230).random()
//                        fat = (40..75).random()
//                    },
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(48.dp),
//                    shape = RoundedCornerShape(999.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = brandGreen,
//                        contentColor = Color.White
//                    )
//                ) {
//                    Text(text = "+  Add Meal")
//                }
//
//                OutlinedButton(
//                    onClick = { /* TODO: connect to device / wearables later */ },
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(48.dp),
//                    shape = RoundedCornerShape(999.dp),
//                    border = ButtonDefaults.outlinedButtonBorder.copy(
//                        brush = Brush.linearGradient(listOf(brandGreen, brandGreen))
//                    ),
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = brandGreen
//                    )
//                ) {
//                    Text(text = "📱  Sync")
//                }
//            }
//
//            // Recent meals
//            Text(
//                text = "Recent Meals",
//                style = MaterialTheme.typography.titleMedium,
//                color = textDark
//            )
//
//            LazyColumn(
//                modifier = Modifier.weight(1f, fill = false),
//                verticalArrangement = Arrangement.spacedBy(10.dp)
//            ) {
//                items(recentMeals) { (title, time, kcal) ->
//                    MealRow(
//                        label = title,
//                        time = time,
//                        calories = kcal,
//                        highlightColor = brandGreen
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun MacroRow(
//    label: String,
//    value: String,
//    progress: Float,
//    barColor: Color
//) {
//    Column {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = label,
//                fontSize = 13.sp,
//                color = Color(0xFF6B7280)
//            )
//            Text(
//                text = value,
//                fontSize = 13.sp,
//                fontWeight = FontWeight.Medium,
//                color = Color(0xFF4B5563)
//            )
//        }
//        Spacer(Modifier.height(4.dp))
//        LinearProgressIndicator(
//            progress = { progress },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(6.dp),
//            color = barColor,
//            trackColor = Color(0xFFE5E7EB)
//        )
//    }
//}
//
//@Composable
//private fun MealRow(
//    label: String,
//    time: String,
//    calories: Int,
//    highlightColor: Color
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(18.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 14.dp, vertical = 10.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
//                Text(
//                    text = label,
//                    fontSize = 15.sp,
//                    fontWeight = FontWeight.Medium,
//                    color = Color(0xFF111827)
//                )
//                Text(
//                    text = time,
//                    fontSize = 12.sp,
//                    color = Color(0xFF6B7280)
//                )
//            }
//            Text(
//                text = "$calories cal",
//                fontSize = 14.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = highlightColor
//            )
//        }
//    }
//}


package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NutritionScreen() {
    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)
    val textDark = Color(0xFF111827)

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
            Text(
                text = "Nutrition Overview",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = textDark
            )

            Text(
                text = "Daily summary for your logged meals (placeholder).",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )

            // Summary card
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Today’s Targets",
                        style = MaterialTheme.typography.titleMedium,
                        color = textDark
                    )

                    NutritionRow(label = "Calories", value = "1,850 / 2,000 kcal")
                    NutritionRow(label = "Protein", value = "95 / 120 g")
                    NutritionRow(label = "Carbs", value = "180 / 220 g")
                    NutritionRow(label = "Fats", value = "60 / 70 g")
                }
            }

            // Placeholder for future charts / history
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Charts and history coming soon 📊",
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        }
    }
}

@Composable
private fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF4B5563)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827)
        )
    }
}

