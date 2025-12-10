package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.seefood.ui.SeeFoodViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@Composable
fun LoginScreen(
    vm: SeeFoodViewModel,
    onLoginSuccess: () -> Unit,
    onGoToSignup: () -> Unit
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

    fun validateEmail(input: String): String? {
        return when {
            input.isBlank() -> "Email is required."
            !android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches() ->
                "Invalid email format."
            else -> null
        }
    }

    fun validatePassword(pw: String): String? {
        return if (pw.isBlank()) "Password is required." else null
    }

    val allValid = emailError == null &&
            passwordError == null &&
            email.isNotEmpty() &&
            password.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(lightTop, Color.White)))
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {

        // Logo + title
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
                "SeeFood",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = brandGreen
            )
            Text(
                "Scan • Cook • Track",
                fontSize = 14.sp,
                color = Color(0xFF4F4F4F)
            )
        }

        // Center form
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // EMAIL
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
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            emailError?.let {
                Text(
                    it,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(Modifier.height(16.dp))

            // PASSWORD
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
                visualTransformation = if (isPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible)
                                Icons.Filled.VisibilityOff
                            else
                                Icons.Filled.Visibility,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            passwordError?.let {
                Text(
                    it,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            generalError?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = Color.Red, fontSize = 14.sp)
            }
        }

        // Bottom buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // LOGIN
            Button(
                enabled = allValid && !isLoading,
                onClick = {
                    isLoading = true
                    generalError = null

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { result ->
                            val uid = result.user?.uid
                            if (uid == null) {
                                isLoading = false
                                generalError = "Login failed: missing user id."
                                return@addOnSuccessListener
                            }

                            // Load profile via ViewModel (uses seefood1 DB)
                            vm.loadUserProfileFromFirestore(uid) { ok ->
                                isLoading = false
                                if (ok) {
                                    onLoginSuccess()
                                } else {
                                    // fallback: at least show email
                                    val em = result.user?.email.orEmpty()
                                    vm.setUserProfile("", "", em)
                                    generalError = "Failed to load profile; using email only."
                                    onLoginSuccess()
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            generalError = when (e) {
                                is FirebaseAuthInvalidUserException ->
                                    "Account not found. Please sign up."
                                is FirebaseAuthInvalidCredentialsException ->
                                    "Incorrect password."
                                else -> e.localizedMessage ?: "Login failed."
                            }
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
                if (isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Log In")
                }
            }

            Spacer(Modifier.height(12.dp))

            // GO TO SIGNUP
            Text(
                text = "Don't have an account? Sign up",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(enabled = !isLoading) { onGoToSignup() }
                    .padding(vertical = 8.dp),
                color = brandGreen,
                fontSize = 14.sp
            )
        }
    }
}


