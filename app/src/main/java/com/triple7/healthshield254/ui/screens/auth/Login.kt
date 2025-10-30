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
import com.triple7.healthshield254.navigation.ROUT_REGISTER
import com.triple7.healthshield254.ui.theme.tripleSeven

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current
    val authViewModel = if (!isInPreview) AuthViewModel(navController, context) else null

    val gradientBrush = Brush.verticalGradient(
        listOf(tripleSeven.copy(alpha = 0.9f), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // 🔹 Image above the card
            Image(
                painter = painterResource(id = R.drawable.medicalinsurance),
                contentDescription = "HealthShield Logo",
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .padding(bottom = 20.dp)
            )

            // 🔹 Smaller card dropped down toward bottom
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(min = 400.dp)
                    .padding(bottom = 40.dp),
                shape = CutCornerShape(topStart = 20.dp, bottomEnd = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "HealthShield",
                        fontSize = 36.sp,
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.Bold,
                        color = tripleSeven
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Welcome back!", color = Color.Gray, fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    var emailFocused by remember { mutableStateOf(false) }
                    var passwordFocused by remember { mutableStateOf(false) }

                    val emailBorderColor by animateColorAsState(
                        targetValue = if (emailFocused) tripleSeven else Color.Gray,
                        label = "emailBorder"
                    )

                    val passwordBorderColor by animateColorAsState(
                        targetValue = if (passwordFocused) tripleSeven else Color.Gray,
                        label = "passwordBorder"
                    )

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

                    Spacer(modifier = Modifier.height(12.dp))

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

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (!isInPreview) {
                                authViewModel?.login(email, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = tripleSeven),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Login", color = Color.White, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = {
                        if (!isInPreview) navController.navigate(ROUT_REGISTER)
                    }) {
                        Text("Don't have an account? Sign up", color = tripleSeven)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}