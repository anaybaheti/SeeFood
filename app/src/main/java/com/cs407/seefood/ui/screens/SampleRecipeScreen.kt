package com.cs407.seefood.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SampleRecipeScreen(
    title: String,
    imageRes: Int,
    ingredients: List<String>,
    steps: List<String>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // IMAGE
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        )

        Spacer(Modifier.height(16.dp))

        // TITLE
        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(12.dp))

        // INGREDIENTS
        Text(
            text = "Ingredients",
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF00C27A),
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        ingredients.forEach {
            Text(
                text = "• $it",
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 26.dp, vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // STEPS
        Text(
            text = "Instructions",
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF00C27A),
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        steps.forEachIndexed { i, step ->
            Text(
                text = "${i + 1}. $step",
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 26.dp, vertical = 6.dp)
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}


