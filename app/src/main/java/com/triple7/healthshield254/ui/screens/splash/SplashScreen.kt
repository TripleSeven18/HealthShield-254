package com.triple7.healthshield254.ui.screens.splash

import android.annotation.SuppressLint
import android.graphics.fonts.FontFamily
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.triple7.healthshield254.R
import com.triple7.healthshield254.navigation.ROUT_ONBOARDING1
import com.triple7.healthshield254.navigation.ROUT_REGISTER
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SplashScreen(navController: NavController) {

    // Auto navigate to Register screen after delay
    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate(ROUT_ONBOARDING1) {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Fullscreen Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {

        // Background Lottie animation (soft fade-in)
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier
                .fillMaxSize(0.9f)
                .align(Alignment.Center)
        )

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            // App logo
            Image(
                painter = painterResource(id = R.drawable.medicalinsurance), // Replace with your logo
                contentDescription = "HealthShield Logo",
                modifier = Modifier
                    .size(120.dp)
                    .shadow(10.dp, shape = CircleShape)
            )

            // App Title
            Text(
                text = "HealthShield KE",
                fontSize = 35.sp,
                color = tripleSeven,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )

            // App Tagline
            Text(
                text = "We Are The Health-Shield",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Progress Indicator
            LinearProgressIndicator(
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(6.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Footer Text
            Text(
                text = "Powered by Triple7 Innovations",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(rememberNavController())
}
