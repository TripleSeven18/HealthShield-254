package com.triple7.healthshield254.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

@Composable
fun Onboarding2(navController: NavController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.spreadpositivity))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tripleSeven)
    ) {
        // Top Icon / Watermark
        Image(
            painter = painterResource(id = R.drawable.medicalinsurance),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopStart)
                .padding(16.dp),
            alpha = 0.3f
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(240.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Bottom Pull-up Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Empower Your Wellness",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Join thousands of users ensuring their medicine is safe and authentic with AI.",
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                // Minor scrollable feature cards
                val features = listOf(
                    "Digital Shield Against Counterfeit Medicines." to Color(0xFFFF6A00),
                    "Scan, Verify, and Protect Your Health With Us" to Color(0xFF4A90E2),
                    "AI-Powered Authenticity Checks." to Color(0xFF50E3C2)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    itemsIndexed(features) { _, (text, color) ->
                        Card(
                            modifier = Modifier.size(width = 140.dp, height = 120.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = text,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = color
                                )
                            }
                        }
                    }
                }

                // Get Started Button
                Button(
                    onClick = { navController.navigate(ROUT_HOME) },
                    colors = ButtonDefaults.buttonColors(containerColor = NewBlue),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text("Get Started", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
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
