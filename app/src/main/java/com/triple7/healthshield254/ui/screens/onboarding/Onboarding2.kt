package com.triple7.healthshield254.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.triple7.healthshield254.navigation.ROUT_HOME
import com.triple7.healthshield254.ui.theme.NewBlue
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Onboarding2(navController: NavController) {

    val scrollState = rememberScrollState()

    Scaffold { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {

            // Top-right Icon
            Image(
                painter = painterResource(id = R.drawable.medicalinsurance),
                contentDescription = "Top Right Icon",
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(tripleSeven)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Spacer(modifier = Modifier.height(50.dp))

                // Heading
                Text(
                    text = "Explore HealthShield254 Features",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lottie Animation
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.spreadpositivity))
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .size(250.dp)
                        .padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Large rectangular card containing smaller square feature cards
                FeatureCardsInLargeCardDynamicHeight()

                Spacer(modifier = Modifier.height(120.dp))

                // Continue Button
                Button(
                    onClick = { navController.navigate(ROUT_HOME) },
                    colors = ButtonDefaults.buttonColors(containerColor = NewBlue),
                    shape = CircleShape,
                    modifier = Modifier
                        .height(55.dp)
                        .width(250.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = "Next", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Get Started", textAlign = TextAlign.Center, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FeatureCardsInLargeCardDynamicHeight() {

    val features = listOf(
        "Digital Shield Against Counterfeit Medicines.",
        "Scan, Verify, and Protect Your Health With Us",
        "AI-Powered Authenticity Checks."
    )

    val cardColors = listOf(
        Color(0xFFFF6A00),
        Color(0xFF4A90E2),
        Color(0xFF50E3C2)
    )

    // Large card wrapping smaller cards (height determined by content)
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .wrapContentHeight(), // dynamically expands
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(features.size) { index ->
                    Card(
                        modifier = Modifier
                            .size(120.dp), // Square cards
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColors[index]),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = features[index],
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOnboarding2() {
    Onboarding2(navController = rememberNavController())
}
