package com.triple7.healthshield254.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.triple7.healthshield254.ui.theme.tripleSeven

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Onboarding2(navController: NavController) {

    val pagerState = rememberPagerState(pageCount = { 3 })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // App Heading
        Text(
            text = "Explore HealthShield254 Features",
            color = tripleSeven,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Lottie animation
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.spreadpositivity))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier
                .size(250.dp)
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Image Pager (feature cards)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
        ) { page ->
            val (imageRes, title, description) = when (page) {
                0 -> Triple(
                    R.drawable.medicalinsurance,
                    "AI Medicine Verification",
                    "Instantly scan medicine QR codes and verify authenticity through AI-powered health protection."
                )
                1 -> Triple(
                    R.drawable.nurse,
                    "Trusted Health Insights",
                    "Access accurate drug information and reports to make confident health decisions."
                )
                else -> Triple(
                    R.drawable.medicalinsurance,
                    "Personal Health Dashboard",
                    "Track your scan history, health alerts, and verification results in one secure place."
                )
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = tripleSeven,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Pager dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                val color =
                    if (pagerState.currentPage == index) tripleSeven else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(10.dp)
                        .background(color, shape = CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Get Started Button
        Button(
            onClick = { navController.navigate(ROUT_HOME) },
            colors = ButtonDefaults.buttonColors(containerColor = tripleSeven),
            shape = CircleShape,
            modifier = Modifier
                .height(55.dp)
                .width(250.dp)
        ) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue", textAlign = TextAlign.Center)
        }

        // Skip to home option
        TextButton(onClick = { navController.navigate(ROUT_HOME) }) {
            Text(
                text = "Skip to Home",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOnboarding2() {
    Onboarding2(navController = rememberNavController())
}
