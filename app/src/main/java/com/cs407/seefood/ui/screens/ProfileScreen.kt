//package com.cs407.seefood.ui.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun ProfileScreen() {
//    var notificationsEnabled by remember { mutableStateOf(true) }
//    var remindersEnabled by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(20.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .size(100.dp)
//                .clip(CircleShape)
//                .background(MaterialTheme.colorScheme.surfaceVariant),
//            contentAlignment = Alignment.Center
//        ) { Text("AB", style = MaterialTheme.typography.headlineMedium) }
//
//        Text(
//            "Anay Baheti",
//            style = MaterialTheme.typography.headlineSmall,
//            fontWeight = FontWeight.Bold
//        )
//        Text("SeeFood Member • Joined 2025", style = MaterialTheme.typography.bodyMedium)
//
//        HorizontalDivider(Modifier.padding(vertical = 12.dp))
//
//        Row(
//            Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text("Notifications")
//            Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
//        }
//
//        Row(
//            Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text("Daily Meal Reminders")
//            Switch(checked = remindersEnabled, onCheckedChange = { remindersEnabled = it })
//        }
//
//        HorizontalDivider(Modifier.padding(vertical = 12.dp))
//
//        Button(
//            onClick = { /* TODO: open saved recipes */ },
//            modifier = Modifier.fillMaxWidth()
//        ) { Text("View Saved Recipes") }
//
//        OutlinedButton(
//            onClick = { /* TODO: logout */ },
//            modifier = Modifier.fillMaxWidth()
//        ) { Text("Logout") }
//    }
//}

package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(onLogout: () -> Unit ) {

    var remindersOn by remember { mutableStateOf(true) }
    var darkModeOn by remember { mutableStateOf(false) }

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

            // Header
            Text(
                text = "Profile & Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = textDark
            )

            // Profile card
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
                    // Avatar circle
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E7EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4B5563)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Alex Johnson",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textDark
                        )
                        Text(
                            text = "alex@example.com",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            // Daily Goals card
            Text(
                text = "Daily Goals",
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GoalRow(
                        emoji = "🔥",
                        label = "Calories",
                        value = "2,000 cal"
                    )
                    GoalRow(
                        emoji = "💪",
                        label = "Protein",
                        value = "150g"
                    )
                    GoalRow(
                        emoji = "💧",
                        label = "Water",
                        value = "8 glasses"
                    )
                }
            }

            // Settings card
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
                        onCheckedChange = { remindersOn = it },
                        brandGreen = brandGreen
                    )
                    SettingRow(
                        label = "Dark Mode",
                        checked = darkModeOn,
                        onCheckedChange = { darkModeOn = it },
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
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFEF4444), Color(0xFFEF4444))
                    )
                ),
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
        Text(
            text = "$emoji $label",
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
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF111827)
        )
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
