package com.triple7.healthshield254.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.triple7.healthshield254.navigation.ROUT_HOME
import com.triple7.healthshield254.navigation.ROUT_REGISTER
import com.triple7.healthshield254.ui.theme.tripleSeven

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } // For hide/show password

    val context = LocalContext.current
    val authViewModel = AuthViewModel(navController, context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tripleSeven)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.width(20.dp))

        Image(
            painter = painterResource(id = R.drawable.medicalinsurance),
            contentDescription = "sikiafiti",
            modifier = Modifier
                .size(width = 200.dp, height = 250.dp)
                .clip(shape = RoundedCornerShape(10.dp)),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "HealthShield",
            fontSize = 40.sp,
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.width(30.dp))

        Text(
            text = "Welcome back",
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(400.dp),
            shape = RoundedCornerShape(60.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.width(360.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = tripleSeven,
                        unfocusedLeadingIconColor = tripleSeven
                    ),
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
                    label = { Text(text = "Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Password with custom hide/show icon
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.width(360.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = tripleSeven,
                        unfocusedLeadingIconColor = tripleSeven
                    ),
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "lock") },
                    label = { Text(text = "Password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            val image = if (passwordVisible)
                                painterResource(id = R.drawable.ic_eye_open) // your custom "hide" icon
                            else
                                painterResource(id = R.drawable.ic_eye_open)   // your custom "show" icon
                            Icon(painter = image, contentDescription = "Toggle password visibility")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = {
                    authViewModel.login(email, password)
                    navController.navigate(ROUT_HOME)
                }) {
                    Text(
                        text = "Login",
                        color = tripleSeven
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = { navController.navigate(ROUT_REGISTER) }) {
                    Text(
                        text = "Don't have an account? Sign Up"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}
