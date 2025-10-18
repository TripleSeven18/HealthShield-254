package com.triple7.healthshield254.ui.screens.auth

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigation.NavController
import com.triple7.healthshield254.data.AuthViewModel

@Composable
fun LaunchScreen(navController: NavController) {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current
    val authViewModel = if (!inPreview) AuthViewModel(navController, context) else null

    // Run check when screen is shown
    LaunchedEffect(Unit) {
        if (!inPreview) {
            authViewModel?.checkLoginStatus()
        }
    }

    // Optional: simple splash while checking
    Surface(color = MaterialTheme.colorScheme.primary) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
    }
}