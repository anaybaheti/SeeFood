package com.cs407.seefood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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

@Composable
fun SignupScreen(
    vm: SeeFoodViewModel,
    onSignupSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val brandGreen = Color(0xFF00C27A)
    val lightTop = Color(0xFFE8FFF5)

    val auth = FirebaseAuth.getInstance()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var firstError by remember { mutableStateOf<String?>(null) }
    var lastError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    fun validateName(name: String): String? =
        if (name.isBlank()) "Required" else null

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
        firstError == null && lastError == null &&
                emailError == null && passwordError == null &&
                firstName.isNotBlank() && lastName.isNotBlank() &&
                email.isNotBlank() && password.isNotBlank()

    // ********* LAYOUT FIX: single scrollable Column *********
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(lightTop, Color.White)))
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Top logo + text (no overlap now)
        Column(
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

            Spacer(Modifier.height(20.dp))

            Text(
                "Create Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = brandGreen
            )
            Text(
                "Sign up to start using SeeFood",
                fontSize = 14.sp,
                color = Color(0xFF4F4F4F)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Form
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FIRST NAME
            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    firstError = validateName(firstName)
                    generalError = null
                },
                isError = firstError != null,
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            firstError?.let {
                Text(
                    it,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(Modifier.height(10.dp))

            // LAST NAME
            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                    lastError = validateName(lastName)
                    generalError = null
                },
                isError = lastError != null,
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            lastError?.let {
                Text(
                    it,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(Modifier.height(10.dp))

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

            Spacer(Modifier.height(10.dp))

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
                Spacer(Modifier.height(10.dp))
                Text(it, color = Color.Red, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Bottom section (button + link)
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                enabled = allValid && !isLoading,
                onClick = {
                    isLoading = true
                    generalError = null

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { result ->
                            val uid = result.user?.uid
                            if (uid == null) {
                                isLoading = false
                                generalError = "Sign up failed: missing user id."
                                return@addOnSuccessListener
                            }

                            // Save profile through ViewModel (uses seefood1 DB)
                            vm.saveUserProfileToFirestore(
                                uid = uid,
                                first = firstName,
                                last = lastName,
                                mail = email
                            ) { ok ->
                                isLoading = false
                                if (ok) {
                                    onSignupSuccess()
                                } else {
                                    generalError = "Failed to save profile."
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            generalError = e.localizedMessage ?: "Sign up failed."
                            android.util.Log.e("SignupScreen", "Auth signUp failed", e)
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

            Text(
                text = "Already have an account? Log in",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(enabled = !isLoading) { onBackToLogin() }
                    .padding(vertical = 8.dp),
                color = brandGreen,
                fontSize = 14.sp
            )
        }
    }
}

