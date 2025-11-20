package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)

    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorText by remember { mutableStateOf<String?>(null) }

    // ----------- VALIDATORS -------------
    fun isValidEmail(input: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()

    fun isPasswordValid(pw: String): Boolean {
        if (pw.length < 8) return false
        if (!pw.any { it.isUpperCase() }) return false
        return true
    }

    //-------------------------------------

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

        // Branding (top)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val logoShape = RoundedCornerShape(28.dp)
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.White, logoShape)
                    .border(4.dp, brandGreen, logoShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(8.dp)
                        .background(brandGreen, RoundedCornerShape(4.dp))
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "SeeFood",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = brandGreen
            )
            Text(
                text = "Scan • Cook • Track",
                fontSize = 14.sp,
                color = Color(0xFF4F4F4F)
            )
        }

        // Inputs
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorText = null
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorText = null
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            errorText?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = Color.Red, fontSize = 14.sp)
            }
        }

        // Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {

            // LOGIN
            OutlinedButton(
                onClick = {

                    // ---- VALIDATION BEFORE FIREBASE ----
                    when {
                        !isValidEmail(email) -> {
                            errorText = "Please enter a valid email address."
                            return@OutlinedButton
                        }
                        !isPasswordValid(password) -> {
                            errorText =
                                "Password must be at least 8 characters and include a capital letter."
                            return@OutlinedButton
                        }
                    }

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { onLoginSuccess() }
                        .addOnFailureListener { e ->
                            errorText = e.localizedMessage ?: "Login failed."
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = brandGreen
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Login")
            }

            Spacer(Modifier.height(12.dp))

            // SIGN UP
            Button(
                onClick = {

                    // Validate FIRST
                    when {
                        !isValidEmail(email) -> {
                            errorText = "Please enter a valid email address."
                            return@Button
                        }
                        !isPasswordValid(password) -> {
                            errorText =
                                "Password must be at least 8 characters and include a capital letter."
                            return@Button
                        }
                    }

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { onLoginSuccess() }
                        .addOnFailureListener { e ->
                            errorText = e.localizedMessage ?: "Sign-up failed."
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandGreen,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Filled.PersonAdd, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Sign Up")
            }
        }
    }
}
