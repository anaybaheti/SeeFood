package com.cs407.seefood.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.seefood.R

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
        // Profile picture placeholder
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_round),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        Text("Anay Baheti", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("SeeFood Member • Joined 2025", style = MaterialTheme.typography.bodyMedium)

        Divider(Modifier.padding(vertical = 12.dp))

        // Settings toggles
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

        Divider(Modifier.padding(vertical = 12.dp))

        Button(
            onClick = { /* future: open saved recipes */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Saved Recipes")
        }

        OutlinedButton(
            onClick = { /* future: logout logic */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
