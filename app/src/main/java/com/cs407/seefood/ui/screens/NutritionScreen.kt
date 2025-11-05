package com.cs407.seefood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun NutritionScreen() {
    // Mock daily nutrition data — will later come from backend
    var calories by remember { mutableStateOf(1875) }
    var protein by remember { mutableStateOf(92) }
    var carbs by remember { mutableStateOf(240) }
    var fat by remember { mutableStateOf(58) }

    val todayMeals = listOf(
        "Breakfast: Oatmeal with apples" to 350,
        "Lunch: Chicken rice bowl" to 650,
        "Snack: Yogurt + banana" to 200,
        "Dinner: Tomato omelette" to 675
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nutrition Log", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // Summary card
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Today's Summary", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("Calories: $calories kcal")
                Text("Protein: ${protein} g")
                Text("Carbs: ${carbs} g")
                Text("Fat: ${fat} g")
            }
        }

        // Meals list
        Text("Meals", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(todayMeals) { (meal, kcal) ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(meal, style = MaterialTheme.typography.bodyLarge)
                        Text("$kcal kcal", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                // For now, randomize macros to simulate update
                calories = (1600..2200).random()
                protein = (70..120).random()
                carbs = (200..300).random()
                fat = (40..70).random()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh Nutrition Data")
        }
    }
}
