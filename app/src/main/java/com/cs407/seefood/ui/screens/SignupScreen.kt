package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.seefood.ui.SeeFoodViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignupScreen(
    vm: SeeFoodViewModel,
    onSignupSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)

    val auth = FirebaseAuth.getInstance()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    var passwordVisible by remember { mutableStateOf(false) }
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
        return when {
            pw.length < 8 -> "Minimum 8 characters required."
            !pw.any { it.isUpperCase() } -> "Password needs at least 1 capital letter."
            else -> null
        }
    }

    val allValid =
        firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                emailError == null &&
                passwordError == null &&
                email.isNotEmpty() &&
                password.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(lightTop, Color.White)))
    ) {
        // Single scrollable column so nothing overlaps on small screens
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // LOGO
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

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Create Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = brandGreen
            )
            Text(
                text = "Sign up to start using SeeFood",
                fontSize = 14.sp,
                color = Color(0xFF4F4F4F),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // FIRST NAME
            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    generalError = null
                },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // LAST NAME
            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                    generalError = null
                },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

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
                singleLine = true
            )

            emailError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 2.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = validatePassword(password)
                    generalError = null
                },
                isError = passwordError != null,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.VisibilityOff
                            else
                                Icons.Filled.Visibility,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                }
            )

            passwordError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 2.dp)
                )
            }

            generalError?.let {
                Spacer(Modifier.height(16.dp))
                Text(text = it, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(Modifier.height(24.dp))

            // SIGN UP BUTTON
            Button(
                enabled = allValid && !isLoading,
                onClick = {
                    isLoading = true
                    generalError = null

                    auth.createUserWithEmailAndPassword(email.trim(), password)
                        .addOnSuccessListener {
                            // save profile into ViewModel
                            vm.setUserProfile(
                                first = firstName.trim(),
                                last = lastName.trim(),
                                mail = email.trim()
                            )
                            isLoading = false
                            onSignupSuccess()
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
                    Icon(Icons.Filled.PersonAdd, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Up")
                }
            }

            Spacer(Modifier.height(12.dp))

            // LINK BACK TO LOGIN
            TextButton(onClick = onLoginClick) {
                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Already have an account? Log in")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

