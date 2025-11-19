//package com.cs407.seefood.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun LoginScreen(onLogin: () -> Unit) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(24.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("SeeFood", style = MaterialTheme.typography.headlineLarge)
//        Spacer(Modifier.height(24.dp))
//        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
//        Spacer(Modifier.height(12.dp))
//        OutlinedTextField(
//            value = password, onValueChange = { password = it },
//            label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(24.dp))
//        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) { Text("Continue") }
//    }
//}

package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
fun LoginScreen(onLogin: () -> Unit) {
    // Brand colors for this screen
    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(lightTop, Color.White)
                )
            )
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // Center logo + title + subtitle
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rounded square logo with vertical bar
            val logoShape = RoundedCornerShape(28.dp)
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(color = Color.White, shape = logoShape)
                    .border(width = 4.dp, color = brandGreen, shape = logoShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(8.dp)
                        .background(color = brandGreen, shape = RoundedCornerShape(4.dp))
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "SeeFood",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = brandGreen
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Scan • Cook • Track",
                fontSize = 14.sp,
                color = Color(0xFF4F4F4F)
            )
        }

        // Bottom buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // "Login with Google" – outlined
            OutlinedButton(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = brandGreen
                )
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.AutoMirrored.Filled.Login,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Login")
            }

            Spacer(Modifier.height(12.dp))

            // "Sign Up" – solid green
            Button(
                onClick = onLogin, // if you later add a separate sign-up flow, change this callback
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandGreen,
                    contentColor = Color.White
                )
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.PersonAdd,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Sign Up")
            }
        }
    }
}

