package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.seefood.ui.SeeFoodViewModel
import com.cs407.seefood.ui.DailyGoals

@Composable
fun ProfileScreen(
    vm: SeeFoodViewModel,
    firstName: String,
    lastName: String,
    email: String,
    onLogout: () -> Unit,
    onHome: () -> Unit,
    onRecipes: () -> Unit,
    onNutrition: () -> Unit,
    onProfile: () -> Unit
) {
    val goals by vm.dailyGoals.collectAsState()
    val remindersOn by vm.remindersEnabled.collectAsState()

    var showEditGoals by remember { mutableStateOf(false) }

    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)
    val textDark = Color(0xFF111827)

    val initials = remember(firstName, lastName) {
        val f = firstName.trim().firstOrNull()?.uppercase()
        val l = lastName.trim().firstOrNull()?.uppercase()
        when {
            f != null && l != null -> "$f$l"
            f != null -> f.toString()
            l != null -> l.toString()
            else -> "?"
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
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                "Profile & Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = textDark
            )

            // Profile card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E7EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            initials,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4B5563)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            "${firstName.trim()} ${lastName.trim()}".trim(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textDark
                        )
                        Text(email, fontSize = 13.sp, color = Color(0xFF6B7280))
                    }
                }
            }

            // Daily Goals Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daily Goals", style = MaterialTheme.typography.titleMedium, color = textDark)
                TextButton(onClick = { showEditGoals = true }) { Text("Edit") }
            }

            // Daily Goals Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    GoalRow("🔥", "Calories", "${goals.calories} cal")
                    GoalRow("💪", "Protein", "${goals.proteinGrams} g")
                    GoalRow("🍞", "Carbs", "${goals.carbsGrams} g")
                    GoalRow("🧈", "Fat", "${goals.fatGrams} g")
                }
            }

            // Settings
            Text("Settings", style = MaterialTheme.typography.titleMedium, color = textDark)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingRow(
                        label = "Reminders",
                        checked = remindersOn,
                        onCheckedChange = { vm.setRemindersEnabled(it) },
                        brandGreen = brandGreen
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
            ) {
                Text("↩ Logout", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }

        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            brandGreen = brandGreen,
            current = BottomNavDestination.Profile,
            onHome = onHome,
            onRecipes = onRecipes,
            onNutrition = onNutrition,
            onProfile = onProfile
        )
    }

    if (showEditGoals) {
        EditGoalsDialog(
            current = goals,
            onSave = { c, p, carbs, fat ->
                vm.updateDailyGoals(c, p, carbs, fat)
                showEditGoals = false
            },
            onDismiss = { showEditGoals = false }
        )
    }
}

@Composable
private fun GoalRow(emoji: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$emoji $label", fontSize = 14.sp, color = Color(0xFF4B5563))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SettingRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, brandGreen: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = brandGreen,
                checkedThumbColor = Color.White
            )
        )
    }
}

@Composable
private fun EditGoalsDialog(
    current: DailyGoals,
    onSave: (Int, Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var calories by rememberSaveable { mutableStateOf(current.calories.toString()) }
    var protein by rememberSaveable { mutableStateOf(current.proteinGrams.toString()) }
    var carbs by rememberSaveable { mutableStateOf(current.carbsGrams.toString()) }
    var fat by rememberSaveable { mutableStateOf(current.fatGrams.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Daily Goals") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it },
                    label = { Text("Protein (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it },
                    label = { Text("Carbs (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = fat,
                    onValueChange = { fat = it },
                    label = { Text("Fat (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    calories.toIntOrNull() ?: current.calories,
                    protein.toIntOrNull() ?: current.proteinGrams,
                    carbs.toIntOrNull() ?: current.carbsGrams,
                    fat.toIntOrNull() ?: current.fatGrams
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
