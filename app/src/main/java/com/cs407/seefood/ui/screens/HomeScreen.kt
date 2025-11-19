//package com.cs407.seefood.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun HomeScreen(
//    onScan: () -> Unit,
//    onRecipes: () -> Unit,
//    onNutrition: () -> Unit,
//    onProfile: () -> Unit
//) {
//    Column(Modifier.fillMaxSize().padding(16.dp)) {
//        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
//        Spacer(Modifier.height(16.dp))
//        Button(onClick = onScan, modifier = Modifier.fillMaxWidth()) { Text("Scan Food") }
//        Spacer(Modifier.height(8.dp))
//        Button(onClick = onRecipes, modifier = Modifier.fillMaxWidth()) { Text("Recipe Suggestions") }
//        Spacer(Modifier.height(8.dp))
//        Button(onClick = onNutrition, modifier = Modifier.fillMaxWidth()) { Text("Nutrition Log") }
//        Spacer(Modifier.height(8.dp))
//        Button(onClick = onProfile, modifier = Modifier.fillMaxWidth()) { Text("Profile") }
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
fun HomeScreen(
    onScan: () -> Unit,
    onRecipes: () -> Unit,
    onNutrition: () -> Unit,
    onProfile: () -> Unit
) {
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
        // MAIN SCROLLABLE CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .padding(bottom = 80.dp) // leave space for bottom nav
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Greeting
            Text(
                text = "Hi, Alex! 👋",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = textDark
            )

            // Large scan card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(brandGreen, brandGreen),
                                    radius = 260f
                                ),
                                shape = CircleShape
                            )
                            .clickable { onScan() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.PhotoCamera,
                                contentDescription = "Scan",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Scan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // My Ingredients
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "My Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    color = textDark
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IngredientChip(label = "Tomatoes", emoji = "🍅", brandGreen = brandGreen)
                        IngredientChip(label = "Cheese", emoji = "🧀", brandGreen = brandGreen)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IngredientChip(label = "Eggs", emoji = "🥚", brandGreen = brandGreen)
                        IngredientChip(label = "Milk", emoji = "🥛", brandGreen = brandGreen)
                    }
                }
            }

            // Suggested Recipes
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Suggested Recipes",
                    style = MaterialTheme.typography.titleMedium,
                    color = brandGreen
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RecipeCard(
                            title = "Omelette",
                            time = "15 min",
                            brandGreen = brandGreen,
                            onClick = onRecipes
                        )
                        RecipeCard(
                            title = "Caprese",
                            time = "10 min",
                            brandGreen = brandGreen,
                            onClick = onRecipes
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RecipeCard(
                            title = "Pancakes",
                            time = "20 min",
                            brandGreen = brandGreen,
                            onClick = onRecipes
                        )
                        RecipeCard(
                            title = "Pasta",
                            time = "25 min",
                            brandGreen = brandGreen,
                            onClick = onRecipes
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // BOTTOM NAV BAR (4 BUTTONS)
        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            brandGreen = brandGreen,
            onRecipes = onRecipes,
            onNutrition = onNutrition,
            onProfile = onProfile
        )
    }
}

@Composable
private fun IngredientChip(
    label: String,
    emoji: String,
    brandGreen: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, brandGreen.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color(0xFF111827)
            )
        }
    }
}

@Composable
private fun RecipeCard(
    title: String,
    time: String,
    brandGreen: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Placeholder "image" area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        color = Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
            )
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = brandGreen
                )
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    modifier: Modifier = Modifier,
    brandGreen: Color,
    onRecipes: () -> Unit,
    onNutrition: () -> Unit,
    onProfile: () -> Unit
) {
    val inactive = Color(0xFF9CA3AF)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // HOME (current tab – highlighted, no click)
            BottomNavItem(
                icon = Icons.Filled.Home,
                label = "Home",
                color = brandGreen,
                onClick = null // already on Home
            )

            BottomNavItem(
                icon = Icons.Filled.MenuBook,
                label = "Recipes",
                color = inactive,
                onClick = onRecipes
            )

            BottomNavItem(
                icon = Icons.Filled.BarChart,
                label = "Nutrition",
                color = inactive,
                onClick = onNutrition
            )

            BottomNavItem(
                icon = Icons.Filled.Person,
                label = "Profile",
                color = inactive,
                onClick = onProfile
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: (() -> Unit)?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) {
            Modifier.clickable { onClick() }
        } else {
            Modifier
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = color
        )
    }
}

