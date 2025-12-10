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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
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
    val darkModeOn by vm.darkModeEnabled.collectAsState()

    var showEditGoals by remember { mutableStateOf(false) }

    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)
    val textDark = Color(0xFF111827)

    // Derive initials, e.g. "Alex Johnson" -> "AJ"
    val initials = remember(firstName, lastName) {
        val f = firstName.trim().takeIf { it.isNotEmpty() }?.firstOrNull()?.uppercase()
        val l = lastName.trim().takeIf { it.isNotEmpty() }?.firstOrNull()?.uppercase()
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(lightTop, Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Profile & Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = textDark
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                            text = initials,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4B5563)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "${firstName.trim()} ${lastName.trim()}".trim(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textDark
                        )
                        Text(
                            text = email,
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Goals",
                    style = MaterialTheme.typography.titleMedium,
                    color = textDark
                )
                TextButton(onClick = { showEditGoals = true }) {
                    Text("Edit")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GoalRow("🔥", "Calories", "${goals.calories} cal")
                    GoalRow("💪", "Protein", "${goals.proteinGrams}g")
                    GoalRow("💧", "Water", "${goals.waterGlasses} glasses")
                }
            }

            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium,
                color = textDark
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingRow(
                        label = "Reminders",
                        checked = remindersOn,
                        onCheckedChange = { vm.setRemindersEnabled(it) },
                        brandGreen = brandGreen
                    )
                    SettingRow(
                        label = "Dark Mode",
                        checked = darkModeOn,
                        onCheckedChange = { vm.setDarkModeEnabled(it) },
                        brandGreen = brandGreen
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(999.dp),
                border = ButtonDefaults.outlinedButtonBorder,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFEF4444)
                )
            ) {
                Text(
                    text = "↩ Logout",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
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
            onSave = { c, p, w ->
                vm.updateDailyGoals(c, p, w)
                showEditGoals = false
            },
            onDismiss = { showEditGoals = false }
        )
    }
}

@Composable
private fun GoalRow(
    emoji: String,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$emoji $label", fontSize = 14.sp, color = Color(0xFF4B5563))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
    }
}

@Composable
private fun SettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    brandGreen: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color(0xFF111827))
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
    onSave: (Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var caloriesText by rememberSaveable { mutableStateOf(current.calories.toString()) }
    var proteinText by rememberSaveable { mutableStateOf(current.proteinGrams.toString()) }
    var waterText by rememberSaveable { mutableStateOf(current.waterGlasses.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit daily goals") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it },
                    label = { Text("Calories (kcal)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = proteinText,
                    onValueChange = { proteinText = it },
                    label = { Text("Protein (g)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = waterText,
                    onValueChange = { waterText = it },
                    label = { Text("Water (glasses)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val c = caloriesText.toIntOrNull() ?: current.calories
                val p = proteinText.toIntOrNull() ?: current.proteinGrams
                val w = waterText.toIntOrNull() ?: current.waterGlasses
                onSave(c, p, w)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
