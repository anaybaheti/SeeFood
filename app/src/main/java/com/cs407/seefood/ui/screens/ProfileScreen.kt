package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var remindersEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) { Text("AB", style = MaterialTheme.typography.headlineMedium) }

        Text(
            "Anay Baheti",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text("SeeFood Member • Joined 2025", style = MaterialTheme.typography.bodyMedium)

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Notifications")
            Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Daily Meal Reminders")
            Switch(checked = remindersEnabled, onCheckedChange = { remindersEnabled = it })
        }

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        Button(
            onClick = { /* TODO: open saved recipes */ },
            modifier = Modifier.fillMaxWidth()
        ) { Text("View Saved Recipes") }

        OutlinedButton(
            onClick = { /* TODO: logout */ },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Logout") }
    }
}
