package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)

    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var loginAllowed by remember { mutableStateOf(true) }

    // ---------------- VALIDATION ----------------

    fun validateEmail(input: String): String? {
        return when {
            input.isBlank() -> "Email is required."
            !android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches() -> "Invalid email format."
            else -> null
        }
    }

    fun validatePassword(pw: String): String? {
        return when {
            pw.length < 8 -> "Minimum 8 characters required."
            !pw.any { it.isUpperCase() } -> "Password needs at least 1 capital letter."
            else -> null
        }
    }

    val allValid = emailError == null && passwordError == null && email.isNotEmpty() && password.isNotEmpty()

    //---------------------------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(lightTop, Color.White)))
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {

        // Branding
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

            Text("SeeFood", fontSize = 28.sp, fontWeight = FontWeight.SemiBold, color = brandGreen)
            Text("Scan • Cook • Track", fontSize = 14.sp, color = Color(0xFF4F4F4F))
        }

        // Inputs
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // EMAIL FIELD
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = validateEmail(email)
                    generalError = null
                },
                isError = emailError != null,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            emailError?.let {
                Text(it, color = Color.Red, fontSize = 13.sp, modifier = Modifier.align(Alignment.Start))
            }

            Spacer(Modifier.height(16.dp))

            // PASSWORD FIELD
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = validatePassword(password)
                    generalError = null
                },
                label = { Text("Password") },
                isError = passwordError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle Password"
                        )
                    }
                }
            )

            passwordError?.let {
                Text(it, color = Color.Red, fontSize = 13.sp, modifier = Modifier.align(Alignment.Start))
            }

            generalError?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = Color.Red, fontSize = 14.sp)
            }
        }

        // Bottom buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {

            // LOGIN BUTTON
            OutlinedButton(
                enabled = allValid && loginAllowed && !isLoading,
                onClick = {
                    isLoading = true
                    generalError = null

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            isLoading = false
                            onLoginSuccess()
                        }
                        .addOnFailureListener { e ->
                            isLoading = false

                            when (e) {
                                is FirebaseAuthInvalidUserException -> {
                                    loginAllowed = false
                                    generalError = "Account not found. Please sign up."
                                }

                                is FirebaseAuthInvalidCredentialsException -> {
                                    generalError = "Incorrect password."
                                }

                                else -> generalError = e.localizedMessage ?: "Login failed."
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (loginAllowed) brandGreen else Color.Gray
                )
            ) {
                if (isLoading)
                    CircularProgressIndicator(strokeWidth = 2.dp, color = brandGreen, modifier = Modifier.size(22.dp))
                else {
                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Login")
                }
            }

            Spacer(Modifier.height(12.dp))

            // SIGN UP BUTTON
            Button(
                enabled = allValid && !isLoading,
                onClick = {
                    isLoading = true
                    generalError = null

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            isLoading = false
                            onLoginSuccess()
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            generalError = e.localizedMessage ?: "Sign up failed."
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (loginAllowed) brandGreen else brandGreen.copy(alpha = 0.5f),
                    contentColor = Color.White
                )
            ) {
                if (isLoading)
                    CircularProgressIndicator(strokeWidth = 2.dp, color = Color.White, modifier = Modifier.size(22.dp))
                else {
                    Icon(Icons.Filled.PersonAdd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Up")
                }
            }
        }
    }
}
