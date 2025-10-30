package com.triple7.healthshield254.ui.screens.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.R
import com.triple7.healthshield254.data.AuthViewModel
import com.triple7.healthshield254.navigation.ROUT_LOGIN
import com.triple7.healthshield254.ui.theme.tripleSeven

@Composable
fun RegistrationScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current
    val authViewModel = if (!isInPreview) AuthViewModel(navController, context) else null

    // Gradient background
    val gradientBrush = Brush.verticalGradient(
        listOf(tripleSeven.copy(alpha = 0.9f), Color.White)
    )

    // 🔹 Updated layout: image above, smaller card near bottom
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
        ) {
            // App Image ABOVE the Card
            Image(
                painter = painterResource(id = R.drawable.medicalinsurance),
                contentDescription = "HealthShield Logo",
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Smaller Card positioned lower
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .wrapContentHeight()
                    .padding(vertical = 8.dp),
                shape = CutCornerShape(topStart = 20.dp, bottomEnd = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.Bold,
                        color = tripleSeven
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Focus animations
                    var nameFocused by remember { mutableStateOf(false) }
                    var emailFocused by remember { mutableStateOf(false) }
                    var passwordFocused by remember { mutableStateOf(false) }
                    var confirmPasswordFocused by remember { mutableStateOf(false) }

                    val nameBorderColor by animateColorAsState(
                        targetValue = if (nameFocused) tripleSeven else Color.Gray,
                        label = "nameBorder"
                    )
                    val emailBorderColor by animateColorAsState(
                        targetValue = if (emailFocused) tripleSeven else Color.Gray,
                        label = "emailBorder"
                    )
                    val passwordBorderColor by animateColorAsState(
                        targetValue = if (passwordFocused) tripleSeven else Color.Gray,
                        label = "passwordBorder"
                    )
                    val confirmPasswordBorderColor by animateColorAsState(
                        targetValue = if (confirmPasswordFocused) tripleSeven else Color.Gray,
                        label = "confirmPasswordBorder"
                    )

                    // Name Input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { nameFocused = it.isFocused },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = nameBorderColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = tripleSeven
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { emailFocused = it.isFocused },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = emailBorderColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = tripleSeven
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (passwordVisible)
                                            R.drawable.ic_eye_open
                                        else
                                            R.drawable.ic_eye_open
                                    ),
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = tripleSeven
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { passwordFocused = it.isFocused },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = passwordBorderColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = tripleSeven
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Confirm Password Input
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { confirmPasswordFocused = it.isFocused },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = confirmPasswordBorderColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = tripleSeven
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Register Button
                    Button(
                        onClick = {
                            if (!isInPreview) {
                                authViewModel?.signup(name, email, password, confirmPassword)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = tripleSeven),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Sign up", color = Color.White, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Redirect to Login
                    TextButton(onClick = {
                        if (!isInPreview) navController.navigate(ROUT_LOGIN)
                    }) {
                        Text("Already have an account? Login", color = tripleSeven)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen(rememberNavController())
}