package com.triple7.healthshield254.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.triple7.healthshield254.R
import com.triple7.healthshield254.navigation.ROUT_LOGIN
import com.triple7.healthshield254.ui.theme.triple777
import com.triple7.healthshield254.ui.theme.tripleSeven

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmpassword by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Create the auth handler once per composition
    val authViewModel = remember { AuthViewModel(navController, context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tripleSeven),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Image (was previously attempted as Lottie) - replace with your desired resource
        Image(
            painter = painterResource(id = R.drawable.medicalinsurance),
            contentDescription = "App animation / logo",
            modifier = Modifier.size(width = 200.dp, height = 250.dp).clip(shape = RoundedCornerShape(10.dp)),
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(700.dp),
            shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "HealthShield",
                    fontSize = 40.sp,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold,
                    color = triple777
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Join us and start your journey today", fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))

                // Username
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.width(360.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = tripleSeven,
                        unfocusedLeadingIconColor = tripleSeven
                    ),
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "person") },
                    placeholder = { Text(text = "Username") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(10.dp))

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
                    placeholder = { Text(text = "Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.width(360.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = tripleSeven,
                        unfocusedLeadingIconColor = tripleSeven
                    ),
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "lock") },
                    placeholder = { Text(text = "Password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Confirm Password
                OutlinedTextField(
                    value = confirmpassword,
                    onValueChange = { confirmpassword = it },
                    modifier = Modifier.width(360.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = tripleSeven,
                        unfocusedLeadingIconColor = tripleSeven
                    ),
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "lock") },
                    placeholder = { Text(text = "Confirm Password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(10.dp))
                val context = LocalContext.current
                val authViewModel = AuthViewModel(navController, context)
                Button(
                    onClick = {

                        navController.navigate(ROUT_LOGIN)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(triple777),
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp)
                ) {
                    Text(text = "Create An Account")
                }




                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = { navController.navigate(ROUT_LOGIN) }) {
                    Text(text = "Already have an account? Login")
                }
            }
        }
    }
}

/**
 * Lightweight Auth handler. Replace with real ViewModel + repository in production.
 * Returns true when signup succeeded (basic validation here and toast feedback).
 */
class AuthViewModel(private val navController: NavController, private val context: Context) {

    fun signup(username: String, email: String, password: String, confirmpassword: String): Boolean {
        // Basic validation
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmpassword.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmpassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        // TODO: place your actual signup / backend call here (Retrofit / Firebase etc.)
        // For now simulate success:
        Toast.makeText(context, "Registration successful for $username", Toast.LENGTH_SHORT).show()
        return true
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(rememberNavController())
}
